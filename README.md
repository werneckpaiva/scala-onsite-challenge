# Simplified Mobile DSP Challenge

## Template

This code is generated using `spray` template project from branch `on_spray-can_1.3_scala-2.11`.

1. Launch SBT:

        $ sbt

2. Compile everything and run all tests:

        > test

3. Start the application:

        > re-start

4. Browse to [http://localhost:8080](http://localhost:8080/)

5. Stop the application:

        > re-stop


## Performance Testing

Run the script `test.sh` in the `performance_testing` folder.

## Implementation

When the application starts, it loads the configuration from a CSV file.


I created a data structure that represents a single campaign. It handles the request for bid and the winning notification.
It also check for campaign requirements and rate limit, but indirectly



