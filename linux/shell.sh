
#修改root密码
sudo passwd root
#切换到root用户
su - root
# 打开这个文件 --需要提前安装vim 如果没有安装 使用vi编辑（不太方便）
vim /etc/ssh/sshd_config

# 修改
#PermitRootLogin prohibit-password
PermitRootLogin yes			# 允许root直接登录
#PermitEmptyPasswords no
PermitEmptyPasswords no		# 因为设置了root密码，所以需要修改为no
# 注释此行
# auth required pam_succeed_if.so user != root quiet_success


# 重启服务
systemctl restart ssh




#安装ufw(若未安装)
sudo apt-get update && sudo apt-get install ufw
#开启ufw
sudo ufw enable
#开放所有端口
sudo ufw allow all
#关闭所有端口
sudo ufw deny all
#开放指定端口
sudo ufw allow 22
#关闭指定端口
sudo ufw deny 22
#查看规则
sudo ufw status
#更新规则
sudo ufw reload
sudo systemctl restart ufw