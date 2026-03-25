package com.example.ecommerceproject.util;

public final class MessageKeys {

    private MessageKeys() {
    }

    public static final String PROTECTED_ADMIN_FIRST_NAME = "System";
    public static final String PROTECTED_ADMIN_LAST_NAME = "Admin";
    public static final String PROTECTED_ADMIN_PASSWORD = "Admin@123";
    public static final String PROTECTED_ADMIN_EMAIL = "admin@ecommerce.com";

    public static final String AUTH_INVALID_CREDENTIALS = "auth.invalid_credentials";
    public static final String AUTH_ACCOUNT_LOCKED = "auth.account_locked";
    public static final String AUTH_ACCOUNT_NOT_ACTIVATED = "auth.account_not_activated";
    public static final String AUTH_PASSWORD_EXPIRED = "auth.password_expired";
    public static final String AUTH_ACCESS_DENIED = "auth.access_denied";
    public static final String AUTH_LOGOUT_SUCCESS = "auth.logout_success";
    public static final String AUTH_TOKEN_REQUIRED = "auth.token_required";
    public static final String AUTH_REGISTRATION_SUCCESS = "auth.registration_success";
    public static final String AUTH_ACTIVATION_SUCCESS = "auth.activation_success";
    public static final String AUTH_ACTIVATION_EXPIRED = "auth.activation_expired";
    public static final String AUTH_RESEND_ACTIVATION_SUCCESS = "auth.resend_activation_success";
    public static final String AUTH_SELLER_REGISTRATION_SUCCESS = "auth.seller_registration_success";
    public static final String AUTH_ADMIN_PROTECTED = "auth.admin_protected";
    public static final String AUTH_LOGIN_SUCCESS = "auth.login_success";
    public static final String AUTH_REFRESH_SUCCESS = "auth.refresh_success";
    public static final String AUTH_INVALID_REFRESH_TOKEN = "auth.invalid_refresh_token";
    public static final String AUTH_REFRESH_TOKEN_REVOKED = "auth.refresh_token_revoked";
    public static final String AUTH_PASSWORD_RESET_SENT = "auth.password_reset_sent";
    public static final String AUTH_PASSWORD_UPDATED = "auth.password_updated";
    public static final String AUTH_INVALID_ACTIVATION_TOKEN = "auth.invalid_activation_token";
    public static final String AUTH_ACCOUNT_ALREADY_ACTIVATED = "auth.account_already_activated";
    public static final String AUTH_USER_NOT_FOUND = "auth.user_not_found";
    public static final String AUTH_USER_NOT_AUTHENTICATED = "auth.user_not_authenticated";
    public static final String AUTH_AUTHENTICATION_REQUIRED = "auth.authentication_required";

    public static final String VALIDATION_FAILED = "validation.failed";
    public static final String VALIDATION_INVALID_JSON_FORMAT = "validation.invalid_json_format";
    public static final String VALIDATION_EMAIL_EXISTS = "validation.email_exists";
    public static final String VALIDATION_GST_EXISTS = "validation.gst_exists";
    public static final String VALIDATION_COMPANY_NAME_EXISTS = "validation.company_name_exists";
    public static final String VALIDATION_PASSWORDS_DO_NOT_MATCH = "validation.passwords_do_not_match";
    public static final String VALIDATION_PASSWORD_DO_NOT_MATCH = "validation.password_do_not_match";
    public static final String VALIDATION_ACCOUNT_NOT_ACTIVATED = "validation.account_not_activated";
    public static final String VALIDATION_INVALID_RESET_TOKEN = "validation.invalid_reset_token";
    public static final String VALIDATION_RESET_TOKEN_USED = "validation.reset_token_used";
    public static final String VALIDATION_USER_ALREADY_DEACTIVATED = "validation.user_already_deactivated";
    public static final String VALIDATION_USER_ALREADY_ACTIVATED = "validation.user_already_activated";
    public static final String VALIDATION_MISSING_REQUIRED_PART = "validation.missing_required_part";
    public static final String VALIDATION_INVALID_ID_FORMAT = "validation.invalid_id_format";
    public static final String VALIDATION_INVALID_PARAMETER_TYPE = "validation.invalid_parameter_type";
    public static final String VALIDATION_CONSTRAINT_VIOLATION = "validation.constraint_violation";
    public static final String VALIDATION_MULTIPART_REQUIRED = "validation.multipart_required";
    public static final String VALIDATION_INVALID_SELLER_ADDRESS_LABEL = "validation.invalid_seller_address_label";
    public static final String VALIDATION_INVALID_CUSTOMER_ADDRESS_LABEL = "validation.invalid_customer_address_label";
    public static final String VALIDATION_SELLER_SINGLE_ADDRESS = "validation.seller_single_address";

    public static final String DIRECTORY_UPLOADS_USERS = "directory.uploads_users";
    public static final String ROLE_CUSTOMER = "role.customer";
    public static final String ROLE_SELLER = "role.seller";

    public static final String RESPONSE_TIMESTAMP = "response.timestamp";
    public static final String RESPONSE_MESSAGE = "response.message";
    public static final String RESPONSE_STATUS = "response.status";
    public static final String RESPONSE_PATH = "response.path";

    public static final String IMAGE_NOT_FOUND = "image.not_found";
    public static final String IMAGE_UPLOAD_SUCCESS = "image.upload_success";
    public static final String IMAGE_UPLOAD_FAILED = "image.upload_failed";
    public static final String IMAGE_FILE_REQUIRED = "image.file_required";
    public static final String IMAGE_FILE_TOO_LARGE = "image.file_too_large";
    public static final String IMAGE_INVALID_FILENAME = "image.invalid_filename";
    public static final String IMAGE_INVALID_FORMAT = "image.invalid_format";
    public static final String IMAGE_PATH_PREFIX = "image.path_prefix";

    public static final String PRODUCT_NAME_EXISTS = "product.name_exists";
    public static final String PRODUCT_MUST_BE_ACTIVE = "product.must_be_active";
    public static final String PRODUCT_PRIMARY_IMAGE_FORMAT = "product.primary_image_format";
    public static final String PRODUCT_SECONDARY_IMAGE_FORMAT = "product.secondary_image_format";
    public static final String PRODUCT_NO_METADATA_FIELDS = "product.no_metadata_fields";
    public static final String PRODUCT_INVALID_METADATA_FIELD = "product.invalid_metadata_field";
    public static final String PRODUCT_INVALID_METADATA_VALUE = "product.invalid_metadata_value";
    public static final String PRODUCT_VARIATION_KEYS_MISMATCH = "product.variation_keys_mismatch";
    public static final String PRODUCT_NOT_FOUND = "product.not_found";
    public static final String PRODUCT_ALREADY_ACTIVE = "product.already_active";
    public static final String PRODUCT_ACTIVATED_SUCCESSFULLY = "product.activated_successfully";
    public static final String PRODUCT_ALREADY_DEACTIVATED = "product.already_deactivated";
    public static final String PRODUCT_DEACTIVATED_SUCCESSFULLY = "product.deactivated_successfully";
    public static final String PRODUCT_MUST_BE_UNIQUE_FOR_BRAND_AND_CATEGORY = "product.must_be_unique_for_brand_and_category";
    public static final String PRODUCT_ADDED_SUCCESSFULLY = "product.added_successfully";
    public static final String PRODUCT_MUST_BE_ACTIVE_TO_ADD_VARIATION = "product.must_be_active_to_add_variation";
    public static final String PRODUCT_VARIATION_CREATED_SUCCESSFULLY = "product.variation_created_successfully";
    public static final String PRODUCT_DELETED_SUCCESSFULLY = "product.deleted_successfully";
    public static final String PRODUCT_UPDATED_SUCCESSFULLY = "product.updated_successfully";
    public static final String PRODUCT_VARIATION_UPDATED_SUCCESSFULLY = "product.variation_updated_successfully";
    public static final String PRODUCT_VARIATIONS_NOT_AVAILABLE = "product.variations_not_available";

    public static final String ERROR_INTERNAL_SERVER = "error.internal_server";
    public static final String ERROR_SELLER_NOT_FOUND = "error.seller_not_found";
    public static final String ERROR_ROLE_NOT_FOUND = "error.role_not_found";
    public static final String ERROR_ADDRESS_NOT_FOUND = "error.address_not_found";
    public static final String ERROR_CUSTOMER_NOT_FOUND = "error.customer_not_found";
    public static final String ERROR_USER_IS_DELETED = "error.user_is_deleted";
    public static final String ERROR_ADDRESS_PERMISSION_DENIED = "error.address_permission_denied";
    public static final String ERROR_ACCESS_DENIED = "error.access_denied";
    public static final String ERROR_USER_NOT_FOUND = "error.user_not_found";

    public static final String ADMIN_USER_ACTIVATED = "admin.user_activated";
    public static final String ADMIN_USER_DEACTIVATED = "admin.user_deactivated";

    public static final String CUSTOMER_PROFILE_UPDATED = "customer.profile_updated";
    public static final String CUSTOMER_PASSWORD_UPDATED = "customer.password_updated";
    public static final String CUSTOMER_ADDRESS_ADDED = "customer.address_added";
    public static final String CUSTOMER_ADDRESS_UPDATED = "customer.address_updated";
    public static final String CUSTOMER_ADDRESS_DELETED = "customer.address_deleted";

    public static final String SELLER_PROFILE_UPDATED = "seller.profile_updated";
    public static final String SELLER_PASSWORD_UPDATED = "seller.password_updated";
    public static final String SELLER_ADDRESS_UPDATED = "seller.address_updated";

    public static final String EMAIL_ACTIVATION_SUBJECT = "email.activation.subject";
    public static final String EMAIL_SELLER_REGISTRATION_SUBJECT = "email.seller_registration.subject";
    public static final String EMAIL_ACCOUNT_LOCKED_SUBJECT = "email.account_locked.subject";
    public static final String EMAIL_PASSWORD_RESET_SUBJECT = "email.password_reset.subject";
    public static final String EMAIL_PASSWORD_CHANGED_SUBJECT = "email.password_changed.subject";
    public static final String EMAIL_ACCOUNT_ACTIVATED_SUBJECT = "email.account_activated.subject";
    public static final String EMAIL_ACCOUNT_DEACTIVATED_SUBJECT = "email.account_deactivated.subject";
    public static final String EMAIL_PRODUCT_ACTIVATED_SUBJECT = "email.product_activated.subject";
    public static final String EMAIL_PRODUCT_DEACTIVATED_SUBJECT = "email.product_deactivated.subject";
    public static final String EMAIL_PRODUCT_CREATED_SUBJECT = "email.product_created.subject";

    public static final String EMAIL_ACTIVATION_BODY = "email.activation.body";
    public static final String EMAIL_SELLER_REGISTRATION_BODY = "email.seller_registration.body";
    public static final String EMAIL_ACCOUNT_LOCKED_BODY = "email.account_locked.body";
    public static final String EMAIL_PASSWORD_RESET_BODY = "email.password_reset.body";
    public static final String EMAIL_PASSWORD_CHANGED_BODY = "email.password_changed.body";
    public static final String EMAIL_ACCOUNT_ACTIVATED_BODY = "email.account_activated.body";
    public static final String EMAIL_ACCOUNT_DEACTIVATED_BODY = "email.account_deactivated.body";
    public static final String EMAIL_PRODUCT_ACTIVATED_BODY = "email.product_activated.body";
    public static final String EMAIL_PRODUCT_DEACTIVATED_BODY = "email.product_deactivated.body";
    public static final String EMAIL_PRODUCT_CREATED_BODY = "email.product_created.body";

    public static final String JWT_TOKEN_INVALID = "jwt.token_invalid";
    public static final String JWT_TOKEN_EXPIRED = "jwt.token_expired";
    public static final String JWT_TOKEN_MALFORMED = "jwt.token_malformed";
    public static final String JWT_CLAIM_USER_ID = "jwt.claim_user_id";
    public static final String JWT_CLAIM_PWD_UPDATED_AT = "jwt.claim_pwd_updated_at";

    public static final String METADATA_FIELD_VALUE_MUST_BE_UNIQUE = "category.field_value_must_be_unique";
    public static final String METADATA_FIELD_CREATED_SUCCESSFULLY = "metadata.field_created_successfully";
    public static final String METADATA_FIELD_NAME_REQUIRED = "category.field_name_required";
    public static final String METADATA_FIELD_NAME_INVALID = "category.field_name_invalid";
    public static final String METADATA_FIELD_NAME_NO_NUMBERS = "category.field_name_no_numbers";
    public static final String INVALID_PARENT_CATEGORY_ID = "category.invalid_parent_category_id";
    public static final String CATEGORY_NAME_MUST_BE_UNIQUE = "category.name_must_be_unique";
    public static final String CATEGORY_NAME_MUST_BE_UNIQUE_WITHIN_PARENT = "category.name_must_be_unique_within_parent";
    public static final String CATEGORY_NAME_REQUIRED = "category.name_required";
    public static final String CATEGORY_NAME_INVALID = "category.name_invalid";
    public static final String CATEGORY_NAME_NO_NUMBERS = "category.name_no_numbers";
    public static final String CATEGORY_NAME_CANNOT_MATCH_PARENT = "category.name_cannot_match_parent";
    public static final String CATEGORY_CREATED_SUCCESSFULLY = "category.created_successfully";
    public static final String INVALID_CATEGORY_ID = "category.invalid_category_id";
    public static final String CATEGORY_UPDATED_SUCCESSFULLY = "category.updated_successfully";
    public static final String INVALID_METADATA_FIELD_ID = "category.invalid_metadata_field_id";
    public static final String METADATA_FIELD_VALUE_REQUIRED = "category.metadata_field_value_required";
    public static final String METADATA_FIELD_VALUE_DUPLICATE = "category.metadata_field_value_duplicate";
    public static final String METADATA_FIELDS_ADDED_TO_CATEGORY_SUCCESSFULLY = "category.metadata_fields_added_successfully";
    public static final String PARENT_CANNOT_ASSOCIATE_WITH_EXISTING_PRODUCT = "category.parent_cannot_associate_with_existing_product";
    public static final String CATEGORY_MUST_BE_VALID_LEAF = "category.must_be_valid_leaf";
    public static final String INVALID_PRODUCT_ID = "product.invalid_product_id";
    public static final String VARIATION_MUST_HAVE_ONE_VALUE = "variation.must_have_one_value";
    public static final String PRODUCT_VARIATION_ALREADY_EXISTS = "product.variation_already_exists";

    public static final String SORT_FIELD_EMAIL = "sort.field_email";
    public static final String SORT_FIELD_FIRSTNAME = "sort.field_firstname";
    public static final String SORT_FIELD_LASTNAME = "sort.field_lastname";
    public static final String SORT_FIELD_ACTIVE = "sort.field_active";
    public static final String SORT_FIELD_CREATED = "sort.field_created";
    public static final String SORT_FIELD_UPDATED = "sort.field_updated";
    public static final String SORT_FIELD_ID = "sort.field_id";

    public static final String FILE_EXTENSIONS_ALLOWED = "file.extensions_allowed";

    public static final String ADDRESS_NOT_AVAILABLE = "address.not_available";

    public static final String TABLE_CATEGORY_METADATA_FIELD_VALUES = "table.category_metadata_field_values";
    public static final String VALIDATION_ID_SUFFIX = "validation.id_suffix";
    public static final String IMAGE_PART_NAME = "image.part_name";
    public static final String RESOURCE_NOT_FOUND_MESSAGE = "resource.not_found_message";
    public static final String PARENT_CATEGORY_CANNOT_HAVE_METADATA = "parent.metadata_not_allowed";
    public static final String PRODUCT_INVALID_METADATA_VALUES = "product.invalid_metadata_values";
    public static final String PRODUCT_INVALID_METADATA_FIELDS = "product.invalid_metadata_fields";
    public static final String PRODUCT_VARIATION_NOT_FOUND = "product.variation_not_found";

}
