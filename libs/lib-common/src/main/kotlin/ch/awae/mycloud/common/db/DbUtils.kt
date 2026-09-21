package ch.awae.mycloud.common.db

import org.springframework.jdbc.core.simple.JdbcClient

fun <T> JdbcClient.MappedQuerySpec<T>.singleOrNull(): T? = this.optional().orElse(null)