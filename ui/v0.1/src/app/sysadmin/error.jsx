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
import { DefaultRoute, Link, Route, RouteHandler } from 'react-router';
import { StickyContainer, Sticky } from 'react-sticky';
import * as styles from './../css/structure.css';
import auth from "./../component/auth/auth";

export default class error extends React.Component<Props, {}> {
      constructor(props) {
              super(props);
              this.state = {
               loggedIn: auth.loggedIn()
              };
            }
            updateAuth(loggedIn) {
              this.setState({
                loggedIn
              })
            }

            componentWillMount() {
              console.log("local storage !");
              console.log(localStorage);


              auth.onChange = this.updateAuth.bind(this);
              auth.login()
            }



      render() {
        return (
          <StickyContainer>
            <Sticky>
                <Sticky className={styles.header}>
                    <div>
                        <span className={styles.title}> Cloud Career Sheet</span>
                        <div className={styles.menu}>
                        <div className={styles.menuItem}><Link to="/login">Sign in</Link></div>
                        <div className={styles.menuItem}><Link to="/register">Register</Link></div>
                        </div>
                    </div>
                </Sticky>
            </Sticky>

            <div className={styles.body}>
            {this.props.children || <p>Failure to register.</p>}
            </div>

            <div className={styles.footer}>
    	        <div>Copyright (c) 2016</div>
                <div>Cloud Career Sheet Inc.</div>
                <div>All Rights Reserved.</div>
            </div>
          </StickyContainer>
        )
      }
}