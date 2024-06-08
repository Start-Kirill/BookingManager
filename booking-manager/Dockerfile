FROM tomcat:latest
MAINTAINER Kiryl

# Удаляем стандартное ROOT-приложение Tomcat
RUN rm -rf /usr/local/tomcat/webapps/ROOT

# Копируем ваш WAR-файл в директорию webapps/
ADD ./target/BookingManager-1.0-SNAPSHOT.war /usr/local/tomcat/webapps/ROOT.war

# Экспонируем порты
EXPOSE 8080 8089

# Запускаем Tomcat
CMD ["catalina.sh", "run"]