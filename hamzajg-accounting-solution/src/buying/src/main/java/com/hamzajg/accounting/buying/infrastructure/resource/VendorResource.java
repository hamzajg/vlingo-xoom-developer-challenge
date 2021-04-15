package com.hamzajg.accounting.buying.infrastructure.resource;

import com.hamzajg.accounting.buying.infrastructure.VendorData;
import com.hamzajg.accounting.buying.infrastructure.persistence.QueryModelStateStoreProvider;
import com.hamzajg.accounting.buying.infrastructure.persistence.VendorQueries;
import com.hamzajg.accounting.buying.model.vendor.Vendor;
import io.vlingo.xoom.actors.Grid;
import io.vlingo.xoom.common.Completes;
import io.vlingo.xoom.http.Response;
import io.vlingo.xoom.http.ResponseHeader;
import io.vlingo.xoom.http.resource.DynamicResourceHandler;
import io.vlingo.xoom.http.resource.Resource;

import static io.vlingo.xoom.common.serialization.JsonSerialization.serialized;
import static io.vlingo.xoom.http.Response.Status.*;
import static io.vlingo.xoom.http.ResponseHeader.Location;
import static io.vlingo.xoom.http.resource.ResourceBuilder.*;

/**
 * See <a href="https://docs.vlingo.io/vlingo-xoom/xoom-annotations#resourcehandlers">@ResourceHandlers</a>
 */
public class VendorResource extends DynamicResourceHandler {
    private final Grid grid;
    private final VendorQueries $queries;

    public VendorResource(final Grid grid) {
        super(grid.world().stage());
        this.grid = grid;
        this.$queries = QueryModelStateStoreProvider.instance().vendorQueries;
    }

    public Completes<Response> create(final VendorData data) {
        return Vendor.create(grid, data.name, data.activityType)
                .andThenTo(state -> Completes.withSuccess(Response.of(Created, ResponseHeader.headers(ResponseHeader.of(Location, location(state.id))), serialized(VendorData.from(state))))
                        .otherwise(arg -> Response.of(NotFound, location()))
                        .recoverFrom(e -> Response.of(InternalServerError, e.getMessage())));
    }

    public Completes<Response> vendors() {
        return $queries.vendors()
                .andThenTo(data -> Completes.withSuccess(Response.of(Ok, serialized(data))))
                .otherwise(arg -> Response.of(NotFound, location()))
                .recoverFrom(e -> Response.of(InternalServerError, e.getMessage()));
    }

    @Override
    public Resource<?> routes() {
        return resource("VendorResource",
                post("/vendors/create")
                        .body(VendorData.class)
                        .handle(this::create),
                get("/vendors")
                        .handle(this::vendors)
        );
    }

    private String location() {
        return location("");
    }

    private String location(final String id) {
        return "/vendors/" + id;
    }


}
