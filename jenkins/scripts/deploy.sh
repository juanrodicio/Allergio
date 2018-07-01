jps -v | grep $1 | awk \'{print $1}\' | xargs kill || true
BUILD_ID=dontKillMe env SERVER.PORT=8081 nohup java -jar -Dspring.profiles.active=prod ./target/$1-$2.jar > /dev/null 2>&1 &
