# Compile

cd thisDir
mvn clean install

# Run

cd target

jar -xvf ***.jar ** This step is actually not required. I have not tried what is the better way to include native library

java -jar /Users/itomao/git/GML_SaaS/AnimationSystem/target/AnimationSystem-1.0-SNAPSHOT.jar