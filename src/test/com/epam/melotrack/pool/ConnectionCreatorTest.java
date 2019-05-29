package com.epam.melotrack.pool;

import org.junit.Assert;
import org.testng.annotations.Test;

import java.util.Collection;

import static org.testng.Assert.*;

public class ConnectionCreatorTest {

    @Test
    public void testCreateConnections() {
        int connectionsAmount = -1;
        Collection<ProxyConnection> connections = ConnectionCreator.createConnections(connectionsAmount);
        Assert.assertNull(connections);
    }
}