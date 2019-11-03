# Scala Data Serialization by Example

Demonstrate conversion to and from several common data serialization
formats from Scala.  Examples include:

 * JSON - [circe]


## Converting GPX file to CSV

Load with [gpsprune] and then save as text.

## Converting GPS CSV to `telemetry.csv`


    API_KEY=e5**********61 sbt ';data/run'                                       

where `API_KEY` is a [LocationIQ] API key.

[circe]: https://circe.github.io/circe/
[gpsprune]: https://activityworkshop.net/software/gpsprune/
[LocationIQ]: https://locationiq.com/
