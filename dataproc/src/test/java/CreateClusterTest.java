/*
 * Copyright 2019 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import static junit.framework.TestCase.assertNotNull;
import static org.hamcrest.MatcherAssert.assertThat;

import com.google.api.gax.longrunning.OperationFuture;
import com.google.cloud.dataproc.v1.ClusterControllerClient;
import com.google.cloud.dataproc.v1.ClusterControllerSettings;
import com.google.cloud.dataproc.v1.ClusterOperationMetadata;
import com.google.protobuf.Empty;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import org.hamcrest.CoreMatchers;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class CreateClusterTest {

  private static final String BASE_CLUSTER_NAME = "test-cluster";
  private static final String REGION = "us-central1";

  private static String projectId = System.getenv("GOOGLE_CLOUD_PROJECT");;
  private String clusterName;
  private ByteArrayOutputStream bout;

  private static void requireEnv(String varName) {
    assertNotNull(
        System.getenv(varName),
        String.format("Environment variable '%s' is required to perform these tests.", varName)
    );
  }

  @BeforeClass
  public static void checkRequirements() {
    requireEnv("GOOGLE_APPLICATION_CREDENTIALS");
    requireEnv("GOOGLE_CLOUD_PROJECT");
  }

  @Before
  public void setUp(){
    clusterName = String.format("%s-%s", BASE_CLUSTER_NAME, UUID.randomUUID().toString());

    bout = new ByteArrayOutputStream();
    System.setOut(new PrintStream(bout));
  }

  @Test
  public void createClusterTest() throws Exception {
    CreateCluster.createCluster(projectId, REGION, clusterName);
    String output = bout.toString();

    assertThat(output, CoreMatchers.containsString(clusterName));
  }

  @After
  public void tearDown() throws IOException {
    try (ClusterControllerClient clusterControllerClient = ClusterControllerClient
        .create()) {
      OperationFuture<Empty, ClusterOperationMetadata> deleteClusterAsyncRequest = clusterControllerClient
          .deleteClusterAsync(projectId, REGION, clusterName);
      deleteClusterAsyncRequest.get();

    } catch (IOException | InterruptedException | ExecutionException e) {
      System.out.println("Error during cluster deletion: \n" + e.toString());
    }
  }
}