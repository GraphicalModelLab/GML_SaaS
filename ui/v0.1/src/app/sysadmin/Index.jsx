import * as React from 'react';
import * as ReactDOM from 'react-dom';
import { browserHistory, Router, Route, Link, withRouter,hashHistory } from 'react-router'
import Login from "./../../component/auth/login";
import Register from "./../../component/auth/register";
import Logout from "./../../component/auth/logout";
import AccountManagement from "./../../component/accountManagement/accountManagement";
import CompanyRegistration from "./../../component/companyRegistration/companyRegistration";
import Top from "./top";
import TopNoLogin from "./topNoLogin";
import App from "./app";

ReactDOM.render(<Router history={hashHistory}>
                    <Route path="/" component={App}>
                      <Route path="top" component={Top} />
                      <Route path="topNoLogin" component={TopNoLogin} />
                      <Route path="register" component={Register} type="sysadmin"/>
                      <Route path="login" component={Login} type="sysadmin"/>
                      <Route path="logout" component={Logout} />
                      <Route path="accountManagement" component={AccountManagement} />
                      <Route path="companyRegistration" component={CompanyRegistration} />
                    </Route>
                  </Router>, document.getElementById('app'));
