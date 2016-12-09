FROM java:8
ADD build/libs/fint-hrapp-mockadapter-*.jar /data/app.jar
CMD ["java", "-jar", "/data/app.jar"]