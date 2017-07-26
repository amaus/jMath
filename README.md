## jMath

This is a library for mathematical concepts that tend to come in useful
when writing programs:

* Linear Algebra
* Graph Theory
* Anything else that may be useful

## Installation

You will need to install the gradle package manager onto your machine

1. Clone the project
2. `gradle install`
3. create new environment variable JMATHDIR and set it to location to location of jMath source
4. Add to your CLASSPATH $JMATHDIR/build/lib/jMath.jar (if you want to import jMath into your own projects)
5. Add to your PATH $JMATHDIR/build/executables
6. Now you can import the library into any java project you write, and you can run any of the
   executables provided by jMath.

## Usage
This project is managed by gradle.

to build:

`gradle build`

to build javadoc:

`gradle javadoc`

to see list of tasks you can do with gradle (essentially get help):

`gradle tasks`

if you issue the command:

`gradle install`

gradle will do a couple things. It will create and install the jar and
executables to your local maven repository (for archival purposes).
The maven local repository is located in ~/.m2/respository.
It will also generate the javadocs, zip them up, and install them to the
$JMATHDIR/distributions and the maven local repository.

Workflow, as developing, compile and test using `gradle build`. When it is
time to make a minor release, use `gradle install` to install jars,
executables, and javadoc archive to local maven repository.

If you are developing within jMath, when you create a new executable, you must
add it to the build.gradle script for it to compile (see the end of build.gradle).
The executable start scripts will ignore your CLASSPATH in lieu of the latest
compiled jar in $JMATHDIR/build/lib/. If you are developing outside of jMath,
that is writing your own programs that depend on jMath, you need to make
sure your CLASSPATH includes the location of your jMath jar.

## javadoc

You can generate the latest javadocs via:

`gradle javadoc`

They will be located within the build/docs/javadoc directory in the project.

Javadoc for the most recent minor release of the library can be found at [jmath.aaronpmaus.com](http://jmath.aaronpmaus.com).

## History
This project was started in June 2016.

## About Versioning
This project uses Semantic Versioning. See http://semver.org

## Credits
Aaron Maus

## License
TODO: Add license
