package edu.berkeley.sparrow.daemon.scheduler;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import com.google.common.base.Optional;

import edu.berkeley.sparrow.thrift.InternalService;
import edu.berkeley.sparrow.thrift.TTaskSpec;

/***
 * A {@link TaskPlacer} implementation which randomly distributes tasks accross
 * backends. Note that if there are fewer backends than tasks, this will distributed multiple
 * tasks on some backends.
 */
public class RandomTaskPlacer implements TaskPlacer {
  
  @Override
  public Collection<TaskPlacementResponse> placeTasks(String appId,
      Collection<InetSocketAddress> nodes, Collection<TTaskSpec> tasks)
      throws IOException {
    Collection<TaskPlacementResponse> out = new HashSet<TaskPlacementResponse>();
    
    ArrayList<InetSocketAddress> orderedNodes = new ArrayList<InetSocketAddress>(nodes);
    Collections.shuffle(orderedNodes);
    
    // Empty client used for all responses
    Optional<InternalService.AsyncClient> client = Optional.absent();
   
    int i = 0;
    for (TTaskSpec task : tasks) {
      InetSocketAddress addr = orderedNodes.get(i++ % nodes.size());
      TaskPlacementResponse response = new TaskPlacementResponse(task,
          addr, client);
      out.add(response);
    }
    return out;
  }
}