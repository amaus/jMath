## jMath

This is a library for mathematical concepts that tend to come in useful
when writing programs:

* Linear Algebra
* Graph Theory
* Anything else we decide to add

## Installation

You will need to install the gradle package manager onto your machine

1. Clone the project
2. `gradle install`
3. create new environment variable JMATHDIR and set it to location to location of jMath source
4. Add to CLASSPATH $JMATHDIR/build/install/jMath/lib/jMath.jar
5. Add to PATH $JMATHDIR/build/install/jMath/bin/
4. Now you can import the library into any java project you write, and you can run any of the
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

gradle will do several things. It will create and install the jar and
executables to $JMATHDIR/build/install/jMath and to the local maven repository 
(for archival purposes). The maven local repository is located in ~/.m2/respository
It will also generate the javadocs, zip them up, and install them to the 
$JMATHDIR/distributions and the maven local repository.

## javadoc

You can generate the latest javadocs via:

`gradle javadoc`

They will be located within the build/docs/javadoc directory in the project.

Javadoc for the most recent minor release of the library can be found at [jmath.aaronpmaus.com](http://jmath.aaronpmaus.com).
## Contributing
1. Fork it!
2. Create your feature branch: `git checkout -b my-new-feature`
3. Commit your changes: `git commit -am 'Add some feature'`
4. Push to the branch: `git push origin my-new-feature`
5. Submit a pull request :D

## History
This project was started in June 2016.

## Credits
Aaron Maus

## License
TODO: Write license
