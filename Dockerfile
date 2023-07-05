FROM anapsix/alpine-java
COPY app.jar /home/app.jar
WORKDIR /home
CMD ["java","-jar","/home/app.jar"]
