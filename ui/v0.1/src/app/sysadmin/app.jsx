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
import * as styles from './../../css/structure.css';
import auth from "./../../component/auth/auth";

export default class App extends React.Component<Props, {}> {
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
        auth.onChange = this.updateAuth.bind(this);
        auth.login()
      }

      componentDidMount() {
        if(!this.state.loggedIn){
            this.context.router.push('/topNoLogin')
        }else{
            this.context.router.push('/top')
        }
      }

      render() {
        return (
          <StickyContainer>
            <Sticky>
                <Sticky className={styles.header}>
                    <div>
                        <span className={styles.title}> Graphical Model Lab - System Admin</span>
                        {this.state.loggedIn ? (
                                <div className={styles.menu}>
                                    <div className={styles.menuItem}><Link to={'/accountManagement'}>アカウント管理</Link></div>
                                    <div className={styles.menuItem}><Link to={'/companyRegistration'}>会社登録</Link></div>
                                    <div className={styles.menuItem}><Link to={'/logout'}>ログアウト</Link></div>
                                </div>
                        ) : (
                                <div className={styles.menu}>
                                <div className={styles.menuItem}><Link to={'/login'}>サインイン</Link></div>
                                <div className={styles.menuItem}><Link to={'/register'}>登録</Link></div>
                                </div>
                        )}
                    </div>
                </Sticky>
            </Sticky>

           <div className={styles.body}>
            {this.props.children}
           </div>

            <div className={styles.footer}>
    	        <div>Copyright (c) 2016</div>
                <div>Graphical Model Lab</div>
                <div>All Rights Reserved.</div>
            </div>
          </StickyContainer>
        )
      }
}

App.contextTypes = {
            router: React.PropTypes.object
};