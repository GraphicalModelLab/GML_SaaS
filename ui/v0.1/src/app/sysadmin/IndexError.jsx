import * as React from 'react';
import * as ReactDOM from 'react-dom';
import { browserHistory, Router, Route, Link, withRouter,hashHistory } from 'react-router'
import Login from "./../../component/auth/login";
import Register from "./../../component/auth/register";
import Error from "./error";

ReactDOM.render(<Router history={hashHistory}>
                                    <Route path="/" component={Error}>
                                      <Route path="register" component={Register} />
                                      <Route path="login" component={Login} />
                                    </Route>
                                  </Router>, document.getElementById('app'));
