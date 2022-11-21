package org.acme;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.eclipse.microprofile.openapi.OASFactory;
import org.eclipse.microprofile.openapi.OASFilter;
import org.eclipse.microprofile.openapi.models.Components;
import org.eclipse.microprofile.openapi.models.OpenAPI;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.microprofile.openapi.models.PathItem;
import org.eclipse.microprofile.openapi.models.examples.Example;

import io.quarkus.logging.Log;
import org.eclipse.microprofile.openapi.models.media.Schema;

import javax.ws.rs.core.MediaType;

public class CustomOASFilter implements OASFilter {

    ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void filterOpenAPI(OpenAPI openAPI) {

        //openApi.getComponents() will result in NULL as we don't have any openapi.yaml file.
        Components defaultComponents = OASFactory.createComponents();
        if (openAPI.getComponents() == null) {
            openAPI.setComponents(defaultComponents);
        }

        generateExamples().forEach(openAPI.getComponents()::addExample);

        updateHealthStatusRefWithProgrammaticSchema(openAPI);
    }

    Map<String, Example> generateExamples() {

        Map<String, Example> examples = new LinkedHashMap<>();

        try {

            ClassLoader loader = Thread.currentThread().getContextClassLoader();

            String userJSON = new String(loader.getResourceAsStream("user.json").readAllBytes(), StandardCharsets.UTF_8);
            String customerJson = new String(loader.getResourceAsStream("customer.json").readAllBytes(), StandardCharsets.UTF_8);

            Example userExample = OASFactory.createExample().description("User JSON Example Description").value(objectMapper.readValue(userJSON, ObjectNode.class));

            Example customerExample = OASFactory.createExample().description("Customer JSON Example Description").value(objectMapper.readValue(customerJson, ObjectNode.class));

            examples.put("userExample", userExample);
            examples.put("customerExample", customerExample);

        } catch (IOException ioException) {
            Log.error(ioException);
        }
        return examples;
    }

    void updateHealthStatusRefWithProgrammaticSchema(OpenAPI openAPI) {

        openAPI.getPaths().getPathItems().forEach((String pathName, PathItem pathItem) -> {
            if (pathName.equalsIgnoreCase("/health-check")) {

                Schema dynamicSchema = OASFactory.createSchema().title("Programmatic-MicroProfile").description("dynamic-schema-description").type(Schema.SchemaType.OBJECT).properties(Map.of("custom-field-data", OASFactory.createSchema().description("Information of the service. If the service is down, this holds the information of why it is failed.").type(Schema.SchemaType.OBJECT)));
                openAPI.getComponents().addSchema("Dynamic-MicroProfile", dynamicSchema);

                pathItem.getGET().getResponses().getAPIResponse("200").getContent().getMediaType("application/json").setSchema(dynamicSchema);
            }
        });
    }

    void updateHealthStatusRefByUsingStaticSchema(OpenAPI openAPI) {

        openAPI.getPaths().getPathItems().forEach((String pathName, PathItem pathItem) -> {
            if (pathName.equalsIgnoreCase("/health-check")) {
                Schema staticMicroProfileSchema = openAPI.getComponents().getSchemas().get("Static-MicroProfile");
                pathItem.getGET().getResponses().getAPIResponse("200").getContent().getMediaType(MediaType.APPLICATION_JSON).setSchema(staticMicroProfileSchema);
            }
        });
    }
}
