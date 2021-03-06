/**
 * Copyright 2014 NAVER Corp.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.navercorp.pinpoint.bootstrap.plugin.editor;


/**
 * @author Jongho Moon
 *
 */
public interface MethodEditorExceptionHandler {
    /**
     * Handles exception thrown wile editing method.
     * 
     * If you want to continue editing class, return normally.
     * If you want to stop editing class, throw an exception.
     * 
     * @param targetClassName
     * @param targetMethodName
     * @param targetMethodParameterTypes
     * @param exception
     */
    public void handle(String targetClassName, String targetMethodName, String[] targetMethodParameterTypes, Throwable exception) throws Throwable;
    
    
    public static final MethodEditorExceptionHandler IGNORE = new MethodEditorExceptionHandler() {
        
        @Override
        public void handle(String targetClassName, String targetMethodName, String[] targetMethodParameterTypes, Throwable exception) throws Throwable {
            // do nothing
        }
    };
    
    public static final MethodEditorExceptionHandler IGNORE_NO_SUCH_METHOD = new MethodEditorExceptionHandler() {
        
        @Override
        public void handle(String targetClassName, String targetMethodName, String[] targetMethodParameterTypes, Throwable exception) throws Throwable {
            if (exception instanceof NoSuchMethodException) {
                return;
            }
            
            throw exception;
        }
    };
}
