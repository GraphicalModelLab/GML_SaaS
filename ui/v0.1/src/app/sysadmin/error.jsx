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