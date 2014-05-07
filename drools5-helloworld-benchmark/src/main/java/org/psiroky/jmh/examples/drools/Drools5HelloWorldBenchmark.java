package org.psiroky.jmh.examples.drools;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.io.ResourceFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.GenerateMicroBenchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
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
public class Drools5HelloWorldBenchmark {

    private KnowledgeBase kBase;
    private StatefulKnowledgeSession kSession;

    @Setup
    public void createKBase() {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newClassPathResource("rules/Sample.drl"), ResourceType.DRL);
        if (kbuilder.getErrors().size() > 0) {
            System.err.println(kbuilder.getErrors());
            throw new RuntimeException("Error while creating kbase from the rules file!");
        }

        kBase = KnowledgeBaseFactory.newKnowledgeBase();
        kBase.addKnowledgePackages(kbuilder.getKnowledgePackages());
    }

    @Setup(Level.Invocation)
    public void createKSession() {
        kSession = kBase.newStatefulKnowledgeSession();
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
