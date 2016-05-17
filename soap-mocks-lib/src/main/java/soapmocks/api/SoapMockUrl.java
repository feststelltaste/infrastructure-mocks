/*
Copyright 2016 Peter Bilstein

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
package soapmocks.api;

import static java.lang.annotation.ElementType.TYPE;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation that defines the service URL. Example: <p>
 * <p>
 * {@link SoapMockUrl}("/Webservice/Test") <p>
 * <p>
 * This will make the service available under http://host:port/soap-mocks/Webservice/Test<p>
 * <p>
 */
@Retention(value=RetentionPolicy.RUNTIME)
@Target({TYPE})
public @interface SoapMockUrl {
    
    String value() default "NONE";

}
