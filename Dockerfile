FROM adoptopenjdk/openjdk8
EXPOSE 8080
ADD build/libs/donus-challenge-0.0.1-SNAPSHOT.jar donus.jar
ENTRYPOINT ["java", "$JAVA_OPTS -XX:+UseContainerSupport", "-Xmx300m -Xss512k -XX:CICompilerCount=2", "-Dserver.port=$PORT", "-Dspring.profiles.active=prod", "-jar", "donus.jar"]
