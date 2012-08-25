/*
 *  Copyright (c) 2012 Malhar, Inc.
 *  All Rights Reserved.
 */
package com.malhartech.stream;

import com.malhartech.bufferserver.Buffer;
import com.malhartech.dag.*;
import org.junit.*;

/**
 *
 * @author Chetan Narsude <chetan@malhar-inc.com>
 */
public class AbstractInputAdapterTest
{
  public AbstractInputAdapterTest()
  {
  }

  @BeforeClass
  public static void setUpClass() throws Exception
  {
  }

  @AfterClass
  public static void tearDownClass() throws Exception
  {
  }
  @SuppressWarnings("PackageVisibleField")
  AbstractInputAdapter instance;
  StreamContext context;

  @Before
  public void setUp()
  {
    context = new StreamContext("irrelevant_source", "irrelevant_sink");
    instance = new AbstractInputAdapterImpl();
    // instance.setContext(context);
  }

  @After
  public void tearDown()
  {
    instance = null;
  }

  /**
   * Test of resetWindow method, of class AbstractInputAdapter.
   */
  @Test
  public void testResetWindow()
  {
    System.out.println("resetWindow");

    final int baseSeconds = 0xcafebabe;
    final int intervalMillis = 0x1234abcd;

    context.setSink(new Sink()
    {
      /**
       *
       * @param t the value of t
       */
      @Override
      public void sink(Object t)
      {
        assert (t.getType() == Buffer.Data.DataType.RESET_WINDOW);
        assert (t.getWindowId() == 0xcafebabe00000000L);
        assert (((ResetWindowTuple) t).getBaseSeconds() == baseSeconds);
        assert (((ResetWindowTuple) t).getIntervalMillis() == intervalMillis);
      }
    });


    instance.resetWindow(baseSeconds, intervalMillis);
  }

//  /**
//   * Test of beginWindow method, of class AbstractInputAdapter.
//   */
//  @Test
//  public void testBeginWindow()
//  {
//    System.out.println("beginWindow");
//    int windowId = 0;
//    AbstractInputAdapter instance = new AbstractInputAdapterImpl();
//    instance.beginWindow(windowId);
//    // TODO review the generated test code and remove the default call to fail.
//    fail("The test case is a prototype.");
//  }
//
//  /**
//   * Test of endWindow method, of class AbstractInputAdapter.
//   */
//  @Test
//  public void testEndWindow()
//  {
//    System.out.println("endWindow");
//    int windowId = 0;
//    AbstractInputAdapter instance = new AbstractInputAdapterImpl();
//    instance.endWindow(windowId);
//    // TODO review the generated test code and remove the default call to fail.
//    fail("The test case is a prototype.");
//  }
//
//  /**
//   * Test of endStream method, of class AbstractInputAdapter.
//   */
//  @Test
//  public void testEndStream()
//  {
//    System.out.println("endStream");
//    AbstractInputAdapter instance = new AbstractInputAdapterImpl();
//    instance.endStream();
//    // TODO review the generated test code and remove the default call to fail.
//    fail("The test case is a prototype.");
//  }
//
//  /**
//   * Test of hasFinished method, of class AbstractInputAdapter.
//   */
//  @Test
//  public void testHasFinished()
//  {
//    System.out.println("hasFinished");
//    AbstractInputAdapter instance = new AbstractInputAdapterImpl();
//    boolean expResult = false;
//    boolean result = instance.hasFinished();
//    assertEquals(expResult, result);
//    // TODO review the generated test code and remove the default call to fail.
//    fail("The test case is a prototype.");
//  }
  @SuppressWarnings("PublicInnerClass")
  public class AbstractInputAdapterImpl extends AbstractInputAdapter
  {
    @Override
    public void setup(StreamConfiguration config)
    {
      throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void activate(StreamContext context)
    {
      throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void teardown()
    {
      throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void deactivate()
    {
      throw new UnsupportedOperationException("Not supported yet.");
    }
  }
}
