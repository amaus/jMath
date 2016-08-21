## jMath

This is a library for mathematical concepts that tend to come in useful
when writing programs:

* Linear Algebra
* Graph Theory
* Anything else we decide to add

## Installation

You will need to install the gradle package manager onto your machine

1. Clone the project
2. gradle install
3. add to classpath location to jMath.jar in ~/.m2/repository/...
4. Now you can import the library into any java project you write.

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

gradle will build a jar of this library and install it into a local maven repository on your machine,
located in ~/m2/repository

You can then find the jar, add its location to your classpath and this library
into any java programs you want to write.

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
