/*
 *  Copyright (c) 2012-2015 DataTorrent, Inc.
 *  All Rights Reserved.
 *
 */
package com.datatorrent.stram.engine;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;

import com.datatorrent.api.*;
import com.datatorrent.api.Context.OperatorContext;
import com.datatorrent.api.Operator.Unifier;

public class Slider implements Unifier<Object>, Operator.IdleTimeHandler, Operator.ActivationListener, StatsListener, Serializable, Operator.CheckpointListener
{
  private List<List<Object>> cache;
  private transient List<Object> currentList;
  private final Unifier<Object> unifier;
  private final int numberOfBuckets;
  private transient int spinMillis;
  final public transient DefaultOutputPort<Object> outputPort = new DefaultOutputPort<Object>();

  public Unifier getUnifier()
  {
    return unifier;
  }

  private Slider()
  {
    unifier = null;
    numberOfBuckets = -1;
  }

  public Slider(Unifier<Object> uniOperator, int buckets)
  {
    unifier = uniOperator;
    cache = new LinkedList<List<Object>>();
    this.numberOfBuckets = buckets;
  }

  private OutputPort getOutputPort()
  {
    for (Class<?> c = unifier.getClass(); c != Object.class; c = c.getSuperclass()) {
      Field[] fields = c.getDeclaredFields();
      for (Field field : fields) {
        field.setAccessible(true);
        try {
          Object portObject = field.get(unifier);
          if (portObject instanceof OutputPort) {
            return (OutputPort) portObject;
          }
        }
        catch (IllegalAccessException e) {
          throw new RuntimeException(e);
        }
      }
    }
    throw new RuntimeException("Unifier should have exactly one output port");
  }

  @Override
  public void process(Object tuple)
  {
    unifier.process(tuple);
    currentList.add(tuple);
  }

  @Override
  public void beginWindow(long windowId)
  {
    unifier.beginWindow(windowId);
    for (List<Object> windowCache : cache) {
      for (Object obj : windowCache) {
        unifier.process(obj);
      }
    }
    currentList = new LinkedList<Object>();
  }

  @Override
  public void endWindow()
  {
    unifier.endWindow();
    cache.add(currentList);
    if (cache.size() == numberOfBuckets) {
      cache.remove(0);
    }
  }

  @Override
  public void setup(OperatorContext context)
  {
    OutputPort unifierOutputPort = getOutputPort();
    unifierOutputPort.setSink(new Sink<Object>()
                              {
                                @Override
                                public void put(Object tuple)
                                {
                                  outputPort.emit(tuple);
                                }

                                @Override
                                public int getCount(boolean reset)
                                {
                                  return 0;
                                }
                              }
    );
    unifier.setup(context);
    spinMillis = context.getValue(OperatorContext.SPIN_MILLIS);
  }

  @Override
  public void teardown()
  {
    unifier.teardown();
  }

  @Override
  public void activate(Context context)
  {
    if (unifier instanceof ActivationListener) {
      ((ActivationListener) unifier).activate(context);
    }

  }

  @Override
  public void deactivate()
  {
    if (unifier instanceof ActivationListener) {
      ((ActivationListener) unifier).deactivate();
    }

  }

  @Override
  public void handleIdleTime()
  {
    if (unifier instanceof IdleTimeHandler) {
      ((IdleTimeHandler) unifier).handleIdleTime();
    }
    else {
      try {
        Thread.sleep(spinMillis);
      }
      catch (InterruptedException ex) {
        throw new RuntimeException(ex);
      }
    }
  }

  @Override
  public Response processStats(BatchedOperatorStats stats)
  {
    if (unifier instanceof StatsListener) {
      return ((StatsListener) unifier).processStats(stats);
    }
    return null;
  }

  @Override
  public void checkpointed(long windowId)
  {
    if (unifier instanceof CheckpointListener) {
      ((CheckpointListener) unifier).checkpointed(windowId);
    }
  }

  @Override
  public void committed(long windowId)
  {
    if (unifier instanceof CheckpointListener) {
      ((CheckpointListener) unifier).committed(windowId);
    }
  }

  private static final long serialVersionUID = 201505251917L;
}
