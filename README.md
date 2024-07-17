# missing-dot: a migration helper library from .NET to Kotlin MPP

![maven repo](https://img.shields.io/maven-central/v/dev.atsushieno/missingdot)

**missing-dot** is a library when you are porting .NET projects to Kotlin multiplatform. It is how we use this library.

## API coverage

missing-dot does not really aim to cover a lot of .NET framework API. This is the list of API that we target and/or cover so far:

- dev.atsushieno.missingdot.xml
  - XmlReader and XmlTextReader (.NET: [System.Xml.XmlReader](https://docs.microsoft.com/en-us/dotnet/api/system.xml.xmlreader))
  - XmlWriter and XmlTextWriter (.NET: [System.Xml.XmlWriter](https://docs.microsoft.com/en-us/dotnet/api/system.xml.xmlwriter))
  - Linq to XML (.NET: [System.Xml.Linq](https://docs.microsoft.com/en-us/dotnet/api/system.xml.linq))

## Using missing-dot

To use this package, add `implementation` line below to your `build.gradle(.kts)`:

```
    dependencies {
        implementation 'dev.atsushieno:missingdot:+' // replace + with the actual version
    }
```

## Resources

We use [GitHub issues](https://github.com/atsushieno/missing-dot/issues) for bug reports etc., and [GitHub Discussions boards](https://github.com/atsushieno/missing-dot/discussions/) open to everyone.

API documentation is published at: https://atsushieno.github.io/missing-dot/

The documentation can be built using `./gradlew dokkaHtml` and it will be generated locally at `build/dokka/html`.

## License

missing-dot is distributed under the MIT License.

