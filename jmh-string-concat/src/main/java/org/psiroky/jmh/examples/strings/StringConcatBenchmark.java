package org.psiroky.jmh.examples.strings;

import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.GenerateMicroBenchmark;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Thread)
public class StringConcatBenchmark {
    /**
     * Several hundreds of various UTF-8 strings in different languages.
     * Taken from https://github.com/nitsanw/jmh-samples/blob/master/src/main/resources/Utf8Samples.txt
     */
    private static final String UTF8_SAMPLES_PATH = "/Utf8Samples.txt";

    private List<String> strings = new ArrayList<>();

    @Param({"100", "1000"})
    private int nrOfConcats;

    @Param({"dummy (just \"a\")", "utf8-samples"})
    private String typeOfStrings;

    @Setup
    public void prepareStrings() {
        if (typeOfStrings.equals("dummy (just \"a\")")) {
            for (int i = 0; i < nrOfConcats; i++) {
                strings.add("a");
            }
        } else if (typeOfStrings.equals("utf8-samples")) {
            loadStringsFromTextFile(UTF8_SAMPLES_PATH);
            // remove or duplicate some items so the list has the expected size
            if (strings.size() > nrOfConcats) {
                strings = strings.subList(0, nrOfConcats);
            } else if (strings.size() < nrOfConcats) {
                for (int i = nrOfConcats / strings.size(); i < 0; i++) {
                    strings.addAll(strings);
                }
                strings = strings.subList(0, nrOfConcats);
            }
            // do a final check that verifies correct number of Strings was generated/loaded
            if (nrOfConcats != strings.size()) {
                throw new RuntimeException("Bug in the benchmark! The expected number of concats (" + nrOfConcats + ") and " +
                        "actual size of the list of Strings (" + strings.size() + ") are not equal!");
            }
        } else {
            throw new RuntimeException("Unknown type of strings to concat: '" + typeOfStrings + "'!");
        }
    }

    private void loadStringsFromTextFile(String resourcePath) {
        try (BufferedReader buffReader = new BufferedReader(
                new InputStreamReader(getClass().getResourceAsStream(resourcePath), "UTF-8"))) {
            String line;
            while ((line = buffReader.readLine()) != null) {
                strings.add(line);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @GenerateMicroBenchmark
    public String concatStringsUsingPlusInForLoop() {
        String result = "";
        for (String str : strings) {
            result += str;
        }
        return result;
    }

    @GenerateMicroBenchmark
    public String concatStringsUsingStringBuffer() {
        StringBuffer stringBuffer = new StringBuffer();
        for (String str : strings) {
            stringBuffer.append(str);
        }
        return stringBuffer.toString();
    }

    @GenerateMicroBenchmark
    public String concatStringsUsingStringBuilder() {
        StringBuilder stringBuilder = new StringBuilder();
        for (String str : strings) {
            stringBuilder.append(str);
        }
        return stringBuilder.toString();
    }

}
