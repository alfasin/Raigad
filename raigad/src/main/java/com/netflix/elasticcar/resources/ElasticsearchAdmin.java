package com.netflix.elasticcar.resources;

import com.google.inject.Inject;
import com.netflix.elasticcar.defaultimpl.IElasticsearchProcess;
import com.netflix.elasticcar.configuration.IConfiguration;
import com.netflix.elasticcar.indexmanagement.ElasticSearchIndexManager;
import com.netflix.elasticcar.utils.SystemUtils;
import org.codehaus.jettison.json.JSONException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;

@Path("/v1/esadmin")
@Produces(MediaType.APPLICATION_JSON)
public class ElasticsearchAdmin 
{
    private static final Logger logger = LoggerFactory.getLogger(ElasticsearchAdmin.class);
    private static final String REST_SUCCESS = "[\"ok\"]";
    private static final String REST_REPOSITORY_NAME = "name";
    private static final String REST_REPOSITORY_TYPE = "type";
    private final IConfiguration config;
    private final IElasticsearchProcess esProcess;
    private final ElasticSearchIndexManager esIndexManager;
    private static final String SHARD_REALLOCATION_PROPERTY = "cluster.routing.allocation.enable";

    @Inject
    public ElasticsearchAdmin(IConfiguration config, IElasticsearchProcess esProcess,ElasticSearchIndexManager esIndexManager)
    {
        this.config = config;
        this.esProcess = esProcess;
        this.esIndexManager = esIndexManager;
    }

    @GET
    @Path("/start")
    public Response esStart() throws IOException, InterruptedException, JSONException
    {
    	logger.info("Starting Elastic Search now through REST call ...");
        esProcess.start(true);
        return Response.ok(REST_SUCCESS, MediaType.APPLICATION_JSON).build();
    }

    @GET
    @Path("/stop")
    public Response esStop() throws IOException, InterruptedException, JSONException
    {
		logger.info("Stopping Elastic Search now through REST call ...");
        esProcess.stop();
        return Response.ok(REST_SUCCESS, MediaType.APPLICATION_JSON).build();
    }

    @GET
    @Path("/shard_allocation_enable/{type}")
    public Response esShardAllocationEnable(@PathParam("type") String type) throws IOException, InterruptedException, JSONException
    {
        logger.info("Enabling Shard Allocation through REST call ...");
        if(!type.equalsIgnoreCase("transient") && !type.equalsIgnoreCase("persistent"))
           throw new IOException("Parameter must be equal to transient or persistent");
        //URL
        String url = "http://127.0.0.1:"+config.getHttpPort()+"/_cluster/settings";
        JSONObject settings = new JSONObject();
        JSONObject property = new JSONObject();
        property.put(SHARD_REALLOCATION_PROPERTY,"all");
        settings.put(type,property);
        String RESPONSE = SystemUtils.runHttpPutCommand(url,settings.toJSONString());
        return Response.ok(RESPONSE, MediaType.APPLICATION_JSON).build();
    }

    @GET
    @Path("/shard_allocation_disable/{type}")
    public Response esShardAllocationDisable(@PathParam("type") String type) throws IOException, InterruptedException, JSONException
    {
        logger.info("Disabling Shard Allocation through REST call ...");
        if(!type.equalsIgnoreCase("transient") && !type.equalsIgnoreCase("persistent"))
            throw new IOException("Parameter must be equal to transient or persistent");
        //URL
        String url = "http://127.0.0.1:"+config.getHttpPort()+"/_cluster/settings";
        JSONObject settings = new JSONObject();
        JSONObject property = new JSONObject();
        property.put(SHARD_REALLOCATION_PROPERTY,"none");
        settings.put(type,property);
        String RESPONSE = SystemUtils.runHttpPutCommand(url,settings.toJSONString());
        return Response.ok(REST_SUCCESS, MediaType.APPLICATION_JSON).build();
    }


    @GET
    @Path("/existingRepositories")
    public Response esExistingRepositories() throws Exception
    {
        logger.info("Retrieving existing repositories through REST call ...");
        //URL
        String URL = "http://127.0.0.1:" + config.getHttpPort() + "/_snapshot/";
        String RESPONSE = SystemUtils.runHttpGetCommand(URL);
        JSONObject JSON_RESPONSE = (JSONObject) new JSONParser().parse(RESPONSE);
        return Response.ok(JSON_RESPONSE, MediaType.APPLICATION_JSON).build();
    }

    /* TODO: Fix this
    @GET
    @Path("/createRepository")
    public Response esCreateRepository(@QueryParam(REST_REPOSITORY_NAME) String repoName,@QueryParam(REST_REPOSITORY_TYPE) String repoType) throws IOException, InterruptedException, JSONException
    {
        logger.info("Creating new Repository through REST call ...");
        if(repoName == null || repoName.isEmpty())
            return Response.ok("\n[\"Repository Name can't be blank\"]\n", MediaType.APPLICATION_JSON).build();
        if(repoType == null || repoType.isEmpty())
            return Response.ok("\n[\"Repository Type can't be blank\"]\n", MediaType.APPLICATION_JSON).build();


        return Response.ok(REST_SUCCESS, MediaType.APPLICATION_JSON).build();
    }
    */


    @GET
    @Path("/run_indexmanager")
    public Response manageIndex()
            throws Exception
    {
        logger.info("Running Index Manager through REST call ...");
        esIndexManager.runIndexManagement();
        return Response.ok(REST_SUCCESS, MediaType.APPLICATION_JSON).build();
    }

}