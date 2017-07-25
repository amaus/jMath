# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com/en/1.0.0/)
and this project adheres to [Semantic Versioning](http://semver.org/spec/v2.0.0.html).

## [Unreleased]
### Added
[None]
### Changed
[None]
### Deprecated
[None]
### Removed
[None]
### Fixed
- Updated dates in CHANGELOG to better conform to ISO 8601
### Security
[None]

## [0.9.0] - 2017-07-18
### Changed
- reorganizing the inheritance hierarchy of Vector and CartesianCoordinates.
  Vector is now the super class. Most of the functionality of
  CartesianCoordinates has been moved into Vector.

## [0.8.0] - 2017-07-17
### Added
- overloaded method addEdge in Graph and Undirected graph to take as parameters
  the objects to wrap as Nodes along with overloaded methods to accept a weight
  as well.
- this CHANGELOG.md
- section to README about semantic versioning. Added section in build.gradle
  for the same purpose
### Changed
- Major Changes in javadocs of all classes to correct or specify version numbers
  of all methods and classes
- minor updates throughout javadocs to use code tags

## [0.7.0] - 2017-04-26
### Added
- added inheritance hierarchy for Max Clique Algorithms. MaxCliqueSolver is
  super class. Under it are MausMaxCliqueSolver, IncMaxCliqueSolver, and
  IncMaxCliqueAdapter. MausMaxCliqueSolver uses a hand rolled algorithm for
  finding max cliques in graphs. IncMaxCliqueSolver is an incomplete
  implementation of Li et al. 2013 Max Clique Algorithm. IncMaxCliqueAdapter
  is a wrapper for Li et al. c program. Of these, IncMaxCliqueAdapter is
  the fastest.
- added MaxIndSetComparator which allows nodes to be ordered by the size
  of the independent set they are contained in. This is used by
  IncMaxCliqueSolver.
- added set method in Node class.
- added get and set GraphFileName in Graph.
### Changed
- BIG CHANGE: changed the generic variable for all graphs to now be
  Comparable. The Objects that are contained by the nodes of the graph must
  be Comparable.
- GraphIO now has the graphs remember the name of the file that they were
  read in from.
- Changed all graph constructors to set or copy the graph file name.
### Removed
- all methods dealing with finding clique, independent sets, or vertex coverings
  in UndirectedGraph. These methods have been moved into Max Clique Algorithms
  Inheritance hierarchy.

## [0.6.0] - 2017-01-17
### Added
- ability to write out dimacs files to GraphIO.
- hashCode and equals methods to Edge
- getEdges() to UndirectedGraph
- UndirectedEdge class that extends Edge
  - include constructors, both weighted and unweighted edges along with a
    constructor that takes an Edge
  - overridden hashCode and equals methods.


## [0.5.0] - 2016-11-12
## Added
- subtract method in Matrix
## Changed
- output in FindClique and FindMaxClique to print the time it took to read in
  a graph from a graph file.
- clarified comments in Graph and UndirectedGraph

## [0.4.1] - 2016-11-11
### Added
- Substantial Changes in build.gradle:
  - a set of tasks dependent on the install task to
    - generate javadocs,
    - zip them up
    - add them as an archive published to the local maven repository
    - create the latest jar in $JMATHDIR/build/lib/ Lastly, that jar file is copied
      and renamed jMath.jar (no version numbers for easy CLASSPATH purposes).
    - and create the executable scripts in $JMATHDIR/build/executables/
  - a task to copy external dependencies into $JMATH/build/lib so that
    executables will work if external dependencies are added.
### Changed
- installation instructions in README to reflect the changes in the build
  process

## [0.4.0] - 2016-11-08
### Added
- initial .gitignore file
- Executables FindClique and FindMaxClique. updated build.gradle to create
  executable scripts for these programs
### Changed
- changed name of method getObject() in Node to get()

## [0.3.0] - 2016-09-20
### Added
- output statements to format output based on level of recursion
- internal helper method to get a deep copy of all the nodes. This simplifies
  getNeighborhood and all instances where we want to get deep copies of the
  Graph.
- getComplementNodes() to return a deep copy of the nodes, but with all the
  edges not in the graph.
- getComplement() to return the complement of the Graph.
### Changed
- renamed findMaxClique that takes the size k to search for to findClique.
- added optimizations to findClique
- refactored getNeighborhood method to simplify and use private helper method.
- cleaned up development print statements

## [0.2.0] - 2016-09-02
### Added
- Copy constructors in Graph, one takes a Graph, the other a Collection
  of Nodes.
- Copy constructors in UndirectedGraph matching those above for Graph
- getNodeAndNeighbors method in Node
- Ability to track the number of recursive calls made when searching for
  a MAX CLIQUE, stores this number in a long because the number of calls
  can get HUGE.

## [0.1.0] - 2016-08-21
### Added
- initial build.gradle for managing this project. Includes basic information on
  project: group, version, plugins (maven, maven-publish, application)
- initial README file
- added class Matrix with constructors, getters for dimensions, rows, columns,
  and elements, multiply by Matrix and scalar, add, transpose, getArray,
  and toString.
- added class CartesianCoordinates with constructors, getters for the
  coordinates, dimension of the coordinates, x, y, and z, coordinate by index,
  moveTo and moveBy methods, a buildCoordsString method, distance to other
  set of coordinates, toString, and a buildIllegalArgumentExceptionString helper
  method.
- added class Vector extends CartesianCoordinates with constructors, dotProduct,
  angle, magnitude, and toString.
- added classes for basic Graph functionality:
  - Node: constructor, numNeighhors, getObject, addEdge for weighted and
    unweighted edges, addNeighbor, removeNeighbor, hasNeighbor, getEdges,
    getNeighbors, hashCode, toString, compareTo, equals
  - Edge: constructors, getters for start, end, weight.
  - Graph: no-arg constructor and constructor that takes the number of nodes,
    size, getNodes, addNode, getNode, addEdge for weighted and unweighted edges,
    removeEdge, getNumEdges (counts the number of edges), numEdges (returns
    the value that should be kept updated through all modifications of this
    graph.), containsNode, removeNodeFromGraph, getNeighborhood, toString, and
    a protected method removeNode. NOTES: the two versions for getting
    the number of edges are for sanity check sake. The protected removeNode is
    not to be called by any class except a subclass. It must be used very
    carefully because it does not properly update the state of the graph. It
    is intended as a helper method.
  - UndirectedGraph extends Graph: no-arg constructor, constructor that takes
    the number of nodes, and a constructor that takes a Graph. addEdge that
    takes two nodes and no weight, removeNodeFromGraph, getNeighborhood,
    findMaxClique, maxPossibleCliqueNumber, isClique
  - GraphIO: contains a static method that reads from a dimacs file and returns
    and UndirectedGraph
