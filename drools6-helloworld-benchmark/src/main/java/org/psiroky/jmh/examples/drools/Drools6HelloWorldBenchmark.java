package org.psiroky.jmh.examples.drools;

import org.kie.api.KieBase;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.internal.builder.conf.RuleEngineOption;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.GenerateMicroBenchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;

import java.util.concurrent.TimeUnit;

/**
 * Drools helloworld example taken from the sample Eclipse Drools project.
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Thread)
public class Drools6HelloWorldBenchmark {

    private KieBase kBase;
    private KieSession kSession;

    @Param({"phreak", "reteoo"})
    private String ruleEngine;

    @Setup
    public void createKBase() {
        KieServices kieServices = KieServices.Factory.get();
        KieContainer container = kieServices.getKieClasspathContainer();

        KieBaseConfiguration kconfig = KieServices.Factory.get().newKieBaseConfiguration();
        kconfig.setOption(RuleEngineOption.determineOption(ruleEngine));
        kBase = container.newKieBase("rules", kconfig);
    }

    @Setup(Level.Invocation)
    public void createKSession() {
        kSession = kBase.newKieSession();
    }

    @GenerateMicroBenchmark
    public int insertFactsAndFireAllRules() {
        Message message = new Message();
        message.setMessage("Hello World");
        message.setStatus(Message.HELLO);
        kSession.insert(message);
        return kSession.fireAllRules();
    }

    @TearDown(Level.Invocation)
    public void disposeKSession() {
        kSession.dispose();
    }

}
