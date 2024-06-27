FROM openjdk:21-jdk-oraclelinux7
VOLUME /tmp
EXPOSE 8080
RUN mkdir -p /SaintMedic/Imagenes && chmod 777 /SaintMedic/Imagenes
ADD /target/ProyectoGrupo4_GTICS-0.0.1-SNAPSHOT.jar proyecto.jar
ENTRYPOINT  ["java" , "-jar" , "proyecto.jar"]
