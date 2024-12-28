package com.gdevxy.blog.dao;

import java.util.function.Function;

import jakarta.ws.rs.NotFoundException;

import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.unchecked.Unchecked;
import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.RowSet;

public abstract class DaoSupport {

	protected <T> Uni<T> as(Uni<RowSet<Row>> request, Function<Row, T> converter) {

		return request
			.onItem().transform(RowSet::iterator)
			.onItem().transform(Unchecked.function(iterator -> {
				if (iterator.hasNext()) {
					return converter.apply(iterator.next());
				}
				throw new NotFoundException("");
			}));
	}

}
