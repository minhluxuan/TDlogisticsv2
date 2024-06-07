# Start with a base image containing Java runtime and Maven
FROM maven:3.9.6 as build
# Make port 8080 available to the world outside this container
EXPOSE 8080

# Set the working directory in the image
WORKDIR /app

# Copy the pom.xml file
COPY ./pom.xml ./

# Download the dependencies
RUN mvn dependency:go-offline -B -X

# Copy the source code
COPY ./ ./

RUN mvn clean package -e -X


# Specify the start-up command
CMD ["mvn", "spring-boot:run", "-e", "-X"]