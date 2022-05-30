
# 1st stage, build the app
FROM maven:3.6-jdk-11 as build

WORKDIR /helidon-jpa

# Create a first layer to cache the "Maven World" in the local repository.
# Incremental docker builds will always resume after that, unless you update
# the pom
ADD pom.xml .
RUN mvn package -Dmaven.test.skip

# Do the Maven build!
# Incremental docker builds will resume here when you change sources
ADD src src
RUN mvn package -DskipTests
RUN echo "done!"

# 2nd stage, build the runtime image
FROM openjdk:11-jre-slim
WORKDIR /helidon-jpa

# Copy the binary built in the 1st stage
COPY --from=build /helidon-jpa/target/helidon-ifms.jar ./
COPY --from=build /helidon-jpa/target/libs ./libs

CMD ["java", "-jar", "helidon-ifms.jar"]

EXPOSE 8080
