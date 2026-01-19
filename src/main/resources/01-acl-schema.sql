-- Spring Security ACL Schema for PostgreSQL / H2
-- This script creates the ACL tables required by Spring Security ACL

-- ACL_CLASS: stores the fully qualified class name of domain objects
CREATE TABLE IF NOT EXISTS acl_class (
    id BIGSERIAL PRIMARY KEY,
    class VARCHAR(255) NOT NULL UNIQUE
);

-- ACL_SID: stores security identities (principals or granted authorities)
CREATE TABLE IF NOT EXISTS acl_sid (
    id BIGSERIAL PRIMARY KEY,
    principal BOOLEAN NOT NULL,
    sid VARCHAR(100) NOT NULL,
    UNIQUE (sid, principal)
);

-- ACL_OBJECT_IDENTITY: stores identity of individual domain object instances
CREATE TABLE IF NOT EXISTS acl_object_identity (
    id BIGSERIAL PRIMARY KEY,
    object_id_class BIGINT NOT NULL,
    object_id_identity VARCHAR(36) NOT NULL,
    parent_object BIGINT,
    owner_sid BIGINT,
    entries_inheriting BOOLEAN NOT NULL,
    UNIQUE (object_id_class, object_id_identity),
    FOREIGN KEY (parent_object) REFERENCES acl_object_identity (id),
    FOREIGN KEY (object_id_class) REFERENCES acl_class (id),
    FOREIGN KEY (owner_sid) REFERENCES acl_sid (id)
);

-- ACL_ENTRY: stores individual permissions assigned to a specific SID for a specific object identity
CREATE TABLE IF NOT EXISTS acl_entry (
    id BIGSERIAL PRIMARY KEY,
    acl_object_identity BIGINT NOT NULL,
    ace_order INTEGER NOT NULL,
    sid BIGINT NOT NULL,
    mask INTEGER NOT NULL,
    granting BOOLEAN NOT NULL,
    audit_success BOOLEAN NOT NULL,
    audit_failure BOOLEAN NOT NULL,
    UNIQUE (acl_object_identity, ace_order),
    FOREIGN KEY (acl_object_identity) REFERENCES acl_object_identity (id),
    FOREIGN KEY (sid) REFERENCES acl_sid (id)
);
