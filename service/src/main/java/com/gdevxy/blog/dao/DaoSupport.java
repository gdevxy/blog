package com.gdevxy.blog.dao;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.function.Function;

import jakarta.ws.rs.NotFoundException;

import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.unchecked.Unchecked;
import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.RowSet;

public abstract class DaoSupport {

	protected <T> Uni<T> asUni(Uni<RowSet<Row>> request, Function<Row, T> converter) {

		return request
			.onItem().transform(RowSet::iterator)
			.onItem().transform(Unchecked.function(iterator -> {
				if (iterator.hasNext()) {
					return converter.apply(iterator.next());
				}
				throw new NotFoundException("");
			}));
	}

	protected Optional<String> asString(Row row, String column) {
		return Optional.ofNullable(row.getString(column));
	}

	protected Instant asRequiredInstant(Row row, String column) {
		return Instant.from(row.getTemporal(column));
	}

	protected OffsetDateTime toOffsetDateTimeUTC(Instant instant) {
		return instant.atOffset(ZoneOffset.UTC);
	}

}
