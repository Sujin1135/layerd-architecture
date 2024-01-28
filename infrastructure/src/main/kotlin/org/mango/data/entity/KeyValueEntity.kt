package org.mango.data.entity

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table(name = "key_values")
data class KeyValueEntity(@Id val key: String, val value: String)