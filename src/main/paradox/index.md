@@@ index

* [Setup](setup/index.md)
* [Usage](usage/index.md)

@@@

# My Documentation

This is my first documentation page using sbt-paradox!

Run `sbt paradox` to generate documentation pages to `target/paradox/site/main/index.html`.

* A linking example to a local readme Markdown file:

Follow reference @ref:[Setup](setup/index.md) for more information.

* A library dependency example:

@@dependency[sbt,Maven,Gradle] {
  group="com.typesafe.akka"
  artifact="akka-http_2.12"
  version="10.0.10"
}

* Snippet inclusion example:

@@snip [ParadoxSnippet.scala](/src/main/scala/ParadoxSnippet.scala) { #snippet_example }
