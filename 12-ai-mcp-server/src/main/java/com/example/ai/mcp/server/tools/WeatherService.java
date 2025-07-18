/*
* Copyright 2024 - 2024 the original author or authors.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* https://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package com.example.ai.mcp.server.tools;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.modelcontextprotocol.spec.McpSchema;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.mcp.McpToolUtils;
import org.springframework.ai.model.ModelOptionsUtils;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class WeatherService {

	private static final String BASE_URL = "https://api.weather.gov";

	private final RestClient restClient;

	public WeatherService() {

		this.restClient = RestClient.builder()
			.baseUrl(BASE_URL)
			.defaultHeader("Accept", "application/geo+json")
			.defaultHeader("User-Agent", "WeatherApiClient/1.0 (your@email.com)")
			.build();
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	public record Points(@JsonProperty("properties") Props properties) {
		@JsonIgnoreProperties(ignoreUnknown = true)
		public record Props(@JsonProperty("forecast") String forecast) {
		}
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	public record Forecast(@JsonProperty("properties") Props properties) {
		@JsonIgnoreProperties(ignoreUnknown = true)
		public record Props(@JsonProperty("periods") List<Period> periods) {
		}

		@JsonIgnoreProperties(ignoreUnknown = true)
		public record Period(@JsonProperty("number") Integer number, @JsonProperty("name") String name,
				@JsonProperty("startTime") String startTime, @JsonProperty("endTime") String endTime,
				@JsonProperty("isDaytime") Boolean isDayTime, @JsonProperty("temperature") Integer temperature,
				@JsonProperty("temperatureUnit") String temperatureUnit,
				@JsonProperty("temperatureTrend") String temperatureTrend,
				@JsonProperty("probabilityOfPrecipitation") Map probabilityOfPrecipitation,
				@JsonProperty("windSpeed") String windSpeed, @JsonProperty("windDirection") String windDirection,
				@JsonProperty("icon") String icon, @JsonProperty("shortForecast") String shortForecast,
				@JsonProperty("detailedForecast") String detailedForecast) {
		}
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	public record Alert(@JsonProperty("features") List<Feature> features) {

		@JsonIgnoreProperties(ignoreUnknown = true)
		public record Feature(@JsonProperty("properties") Properties properties) {
		}

		@JsonIgnoreProperties(ignoreUnknown = true)
		public record Properties(@JsonProperty("event") String event, @JsonProperty("areaDesc") String areaDesc,
				@JsonProperty("severity") String severity, @JsonProperty("description") String description,
				@JsonProperty("instruction") String instruction) {
		}
	}

	/**
	 * Get forecast for a specific latitude/longitude
	 * @param latitude Latitude
	 * @param longitude Longitude
	 * @return The forecast for the given location
	 * @throws RestClientException if the request fails
	 */
	@Tool(description = "Get weather forecast for a specific latitude/longitude")
	public String getWeatherForecastByLocation(double latitude, double longitude) {

		var points = restClient.get()
			.uri("/points/{latitude},{longitude}", latitude, longitude)
			.retrieve()
			.body(Points.class);

		var forecast = restClient.get().uri(points.properties().forecast()).retrieve().body(Forecast.class);

		String forecastText = forecast.properties().periods().stream().map(p -> {
			return String.format("""
					%s:
					Temperature: %s %s
					Wind: %s %s
					Forecast: %s
					""", p.name(), p.temperature(), p.temperatureUnit(), p.windSpeed(), p.windDirection(),
					p.detailedForecast());
		}).collect(Collectors.joining());

		return forecastText;
	}


	/**
	 * The response format from the Open-Meteo API
	 */
	public record WeatherResponse(Current current) {
		public record Current(LocalDateTime time, int interval, double temperature_2m) {
		}
	}

	@Tool(description = "Get the temperature (in celsius) for a specific location")
	public String getTemperature(@ToolParam(description = "The location latitude") double latitude,
								 @ToolParam(description = "The location longitude") double longitude,
								 ToolContext toolContext) {

		WeatherResponse weatherResponse = restClient
				.get()
				.uri("https://api.open-meteo.com/v1/forecast?latitude={latitude}&longitude={longitude}&current=temperature_2m",
						latitude, longitude)
				.retrieve()
				.body(WeatherResponse.class);
		// 获取上下文
		
		String responseWithPoems = callMcpSampling(toolContext, weatherResponse);
		return responseWithPoems;
	}

	public String callMcpSampling(ToolContext toolContext, WeatherResponse weatherResponse) {

		StringBuilder openAiWeatherPoem = new StringBuilder();
		StringBuilder anthropicWeatherPoem = new StringBuilder();
		
		// 获取MCP客户端对象
		McpToolUtils.getMcpExchange(toolContext)
				.ifPresent(exchange -> {
					// 向客户端发送日志消息
					exchange.loggingNotification(McpSchema.LoggingMessageNotification.builder()
							.level(McpSchema.LoggingLevel.INFO)
							.data("Start sampling")
							.build());

					// 如果客户端支持采样，则进行采样
					if (exchange.getClientCapabilities().sampling() != null) {
						var messageRequestBuilder = McpSchema.CreateMessageRequest.builder()
								.systemPrompt("You are a poet!")
								.messages(List.of(new McpSchema.SamplingMessage(McpSchema.Role.USER,
										new McpSchema.TextContent(
												"Please write a poem about thius weather forecast (temperature is in Celsious). Use markdown format :\n "
														+ ModelOptionsUtils
														.toJsonStringPrettyPrinter(weatherResponse)))));

						var opeAiLlmMessageRequest = messageRequestBuilder
								.modelPreferences(McpSchema.ModelPreferences.builder().addHint("openai").build())
								.build();
						McpSchema.CreateMessageResult openAiLlmResponse = exchange.createMessage(opeAiLlmMessageRequest);
						var openAiLlmResponseText = ((McpSchema.TextContent) openAiLlmResponse.content()).text();
						openAiWeatherPoem.append(openAiLlmResponseText);

						var anthropicLlmMessageRequest = messageRequestBuilder
								.modelPreferences(McpSchema.ModelPreferences.builder().addHint("anthropic").build())
								.build();
						McpSchema.CreateMessageResult anthropicAiLlmResponse = exchange.createMessage(anthropicLlmMessageRequest);
						var anthropicAiLlmResponseText = ((McpSchema.TextContent) anthropicAiLlmResponse.content()).text();
						anthropicWeatherPoem.append(anthropicAiLlmResponseText);

					}
					
					
					// 向客户端发送日志消息
					exchange.loggingNotification(McpSchema.LoggingMessageNotification.builder()
							.level(McpSchema.LoggingLevel.INFO)
							.data("Finish Sampling")
							.build());

				});

		String responseWithPoems = "OpenAI poem about the weather: " + openAiWeatherPoem + "\n\n" 
				+ "Anthropic poem about the weather: " + anthropicWeatherPoem + "\n"
				+ "--------------------------------------"
				+ ModelOptionsUtils.toJsonStringPrettyPrinter(weatherResponse);

		log.info("responseWithPoems:{}", responseWithPoems);

		return responseWithPoems;

	}

	/**
	 * Get alerts for a specific area
	 * @param state Area code. Two-letter US state code (e.g. CA, NY)
	 * @return Human readable alert information
	 * @throws RestClientException if the request fails
	 */
	@Tool(description = "Get weather alerts for a US state. Input is Two-letter US state code (e.g. CA, NY)")
	public String getAlerts(String state) {
		Alert alert = restClient.get().uri("/alerts/active/area/{state}", state).retrieve().body(Alert.class);

		return alert.features()
			.stream()
			.map(f -> String.format("""
					Event: %s
					Area: %s
					Severity: %s
					Description: %s
					Instructions: %s
					""", f.properties().event(), f.properties.areaDesc(), f.properties.severity(),
					f.properties.description(), f.properties.instruction()))
			.collect(Collectors.joining("\n"));
	}

	public static void main(String[] args) {
		WeatherService client = new WeatherService();
		System.out.println(client.getTemperature(47.6062, -122.3321,null));
//		System.out.println(client.getWeatherForecastByLocation(47.6062, -122.3321));
//		System.out.println(client.getAlerts("NY"));
	}

}