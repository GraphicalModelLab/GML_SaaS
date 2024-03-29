/**
 * Copyright (C) 2018 Mao Ito
 *
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

import * as React from 'react';
import * as ReactDOM from 'react-dom';
import { browserHistory, Router, Route, Link, withRouter, hashHistory } from 'react-router'
import Login from "./../../component/auth/login";
import Register from "./../../component/auth/register";
import Error from "./error";

ReactDOM.render(<Router history={ hashHistory }>
                  <Route path="/" component={ Error }>
                    <Route path="register" component={ Register } />
                    <Route path="login" component={ Login } />
                  </Route>
                </Router>, document.getElementById('app'));
