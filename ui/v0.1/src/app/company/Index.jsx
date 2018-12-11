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
import { browserHistory, Router, Route, Link, withRouter,hashHistory } from 'react-router'
import Login from "./../../component/auth/login";
import Register from "./../../component/auth/register";
import Logout from "./../../component/auth/logout";
import NotFound from "./../../component/notFound/notFound";
import GraphLab from "./../../component/graphLab/graphLab";
import GraphLabLocalRepository from "./../../component/graphLabLocalRepository/graphLabLocalRepository";
import SearchResult1 from  "./../../component/searchResult/searchResult1";
import SearchResult2 from  "./../../component/searchResult/searchResult2";
import DataExtractor from "./../../component/dataExtractor/dataExtractor";
import SocialConnect from "./../../component/socialConnect/socialConnect";
import AccountManagementIndividual from "./../../component/accountManagementIndividual/accountManagementIndividual";
import AccountManagement from "./../../component/accountManagement/accountManagement";
import CompanyRegistration from "./../../component/companyRegistration/companyRegistration";
import Top from "./top";
import TopNoLogin from "./topNoLogin";
import App from "./app";

ReactDOM.render(<Router history={hashHistory}>
                    <Route path="/" component={App}>
                      <Route path="top" component={Top} />
                      <Route path="topNoLogin" component={TopNoLogin} />
                      <Route path="register" component={Register} />
                      <Route path="login" component={Login}/>
                      <Route path="logout" component={Logout} />
                      <Route path="graphLab" component={GraphLab} editable="true"/>
                      <Route path="graphLabLocalRepository" component={GraphLabLocalRepository} editable="true"/>
                      <Route path="searchResult1" component={SearchResult1} editable="true"/>
                      <Route path="searchResult2" component={SearchResult2} editable="true"/>
                      <Route path="dataExtractor" component={DataExtractor} editable="true"/>
                      <Route path="socialConnect" component={SocialConnect} editable="true"/>
                      <Route path="accountManagementIndividual" component={AccountManagementIndividual} />
                      <Route path="accountManagement" component={AccountManagement} />
                      <Route path="companyRegistration" component={CompanyRegistration} />
                      <Route path="notFound" component={NotFound} />
                    </Route>
                  </Router>, document.getElementById('app'));
