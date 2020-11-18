.SILENT:
.PHONY: build

#########
# Build #
#########

build:
	mvn -DskipTests=true package

#########
# Watch #
#########

watch:
    mvn spring-boot:run
