# angles-java-examples
This repository contains examples of various java test execution framework implementations and how they can integration with Angles.

At the moment we provide libraries for the following java execution frameworks:
- [TestNG](https://github.com/AnglesHQ/angles-java-client/blob/master/angles-testng/README.md)
- [Junit5](https://github.com/AnglesHQ/angles-java-client/blob/master/angles-junit5/README.md)
- [CucumberJVM](https://github.com/AnglesHQ/angles-java-client/blob/master/angles-cucumberjvm/README.md)

If there is no example of an implementation for the framework you're using, you can look at using the [angles-java-core](https://github.com/AnglesHQ/angles-java-client/tree/master/angles-java-core) library directly. There you will find some documentation on how to integrate angles into a framework (as long as it has hooks such as Before, After, etc)

For the main documentation on Angles have a look at our [GitHub page](https://angleshq.github.io/).

### Setup
To be able to run the following tests you will need to have:
- Java 8
- Maven installed
- A local instance of Angles running which has a team "angles" and an environment "qa" setup. It will also need separate components for each of the examples (e.g. junit5-example, cucumberjvm-example, testng-example)  
        - **NOTE**: for help on how to do this, have a look at our [GitHub page](https://angleshq.github.io/).
  
If you would like to change e.g. the team name, component name or the location of the angles server just change the values configured in the pom (or override them in the maven command line e.g. -Dangles.team=your-team)

### Running the tests

Once you have your machine setup and your local instance of Angles setup, just clone this repo and in your terminal navigate to the relevant framework folder.
There you simply run the following command, to trigger the test example which includes a visual test.
```shell
# to trigger the tests with default variables.
> mvn clean test

# if you would like to override some of the default variables use -D maven argument
> mvn clean test -Dangles.team=your_team
```

**NOTE**: The first time the test run, there will be no baseline image. You will have to go to the test results and select the image to make it the baseline. After that it will be used to do the visual comparison.
