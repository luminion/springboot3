

# 

| 关键词                                 | 样本                                          | 逻辑结果                                 |
| ----------------------------------- | ------------------------------------------- | ------------------------------------ |
| `After`                             | `findByBirthdateAfter(Date date)`           | `birthdate > date`                   |
| `GreaterThan`                       | `findByAgeGreaterThan(int age)`             | `age > age`                          |
| `GreaterThanEqual`                  | `findByAgeGreaterThanEqual(int age)`        | `age >= age`                         |
| `Before`                            | `findByBirthdateBefore(Date date)`          | `birthdate < date`                   |
| `LessThan`                          | `findByAgeLessThan(int age)`                | `age < age`                          |
| `LessThanEqual`                     | `findByAgeLessThanEqual(int age)`           | `age <= age`                         |
| `Between`                           | `findByAgeBetween(int from, int to)`        | `age BETWEEN from AND to`            |
| `NotBetween`                        | `findByAgeNotBetween(int from, int to)`     | `age NOT BETWEEN from AND to`        |
| `In`                                | `findByAgeIn(Collection<Integer> ages)`     | `age IN (age1, age2, ageN)`          |
| `NotIn`                             | `findByAgeNotIn(Collection ages)`           | `age NOT IN (age1, age2, ageN)`      |
| `IsNotNull`,`NotNull`               | `findByFirstnameNotNull()`                  | `firstname IS NOT NULL`              |
| `IsNull`,`Null`                     | `findByFirstnameNull()`                     | `firstname IS NULL`                  |
| `Like`, `StartingWith`,`EndingWith` | `findByFirstnameLike(String name)`          | `firstname LIKE name`                |
| `NotLike`,`IsNotLike`               | `findByFirstnameNotLike(String name)`       | `firstname NOT LIKE name`            |
| `Containing`在字符串上                   | `findByFirstnameContaining(String name)`    | `firstname LIKE '%' + name +'%'`     |
| `NotContaining`在字符串上                | `findByFirstnameNotContaining(String name)` | `firstname NOT LIKE '%' + name +'%'` |
| `(No keyword)`                      | `findByFirstname(String name)`              | `firstname = name`                   |
| `Not`                               | `findByFirstnameNot(String name)`           | `firstname != name`                  |
| `IsTrue`,`True`                     | `findByActiveIsTrue()`                      | `active IS TRUE`                     |
| `IsFalse`, `False`                  | `findByActiveIsFalse()`                     | `active IS FALSE`                    |
