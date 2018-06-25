import * as React from 'react';
import * as ReactDOM from 'react-dom';
import Top from "./top";

import { DefaultRoute, Link, Route, RouteHandler } from 'react-router';
import { StickyContainer, Sticky } from 'react-sticky';
import * as styles from './../../css/structure.css';
import auth from "./../../component/auth/auth";
import Loading from './../../component/loader/loading';
import $ from 'jquery';

export default class App extends React.Component<Props, {}> {

      constructor(props) {
        super(props);
        this.state = {
         loggedIn: auth.loggedIn()
        };

        this.searchEngineer = this.searchEngineer.bind(this);

        $.ajax({
                type:"get",
                url:"../commonData/autocomplete/list.json",
                dataType: "json",
                success: function(json_data) {
                    autocompleteLanguage = json_data.language;
                    autocompleteServeros = json_data.serveros;
                    autocompleteOs = json_data.os;
                    autocompleteFrameworkmiddleware = json_data.frameworkmiddleware;
                    autocompleteDatabase = json_data.database;
                    autocompleteNetwork = json_data.network;
                    autocompleteDesignerSoftware = json_data.designersoftware;
                    autocompleteContractForm = json_data.contractform;
                },
                error: function (request, status, error) {
                },
                complete: function() {
                }
            });
      }

      updateAuth(loggedIn) {
        this.setState({
          loggedIn
        })
      }

      componentWillMount() {
        auth.onChange = this.updateAuth.bind(this);
        auth.login();
      }

      componentDidMount() {
        if(!this.state.loggedIn){
            this.context.router.push('/'+"topNoLogin")
        }else{
            this.context.router.push('/'+"top")
        }
      }

      onOpenChange(openKeys) {

            console.log('onOpenChange', openKeys);
      }

      searchEngineer(e) {
            e.preventDefault();

            this.refs.loading.openModal();

            var data = {
                         companyid: auth.getCompanyid(),
                         userid:auth.getUserid(),
                         token: auth.getToken(),
                         code:10,
                         keyword: this.refs.search.value
            };

            var parent = this;
            $.ajax({
                type:"post",
                url:"php/modules/Company.php/engineer/search",
                data:JSON.stringify(data),
                contentType: 'application/json',
                dataType: "json",
                success: function(json_data) {
                   // React query convert an array with only one element to an string type data so that push one dummy data to keep it as an array

                   parent.refs.loading.closeModal();
                   json_data.body.userids.unshift("");
                   parent.context.router.push(
                   {
                     pathname: '/searchresult',
                     query: { ids:  json_data.body.userids }
                   })

                },
                error: function (request, status, error) {
                },
                complete: function() {
                }
            });
      }

      render() {
        return (
          <StickyContainer>
            <Sticky>
                <Sticky className={styles.header}>
                    <div className={styles.menuHeader}>
                        <img className={styles.menuItemLogo} src='../icon/infographic/ccs_logo.png'/>

                        {this.state.loggedIn ? (
                            (auth.getRole() == 'sysadmin' ? (
                                <div className={styles.menu}>
                                    <div className={styles.menuItem}><Link to={'/logout'}>ログアウト</Link></div>
                                    <div className={styles.menuItem}><Link to={'/companyRegistration'}>会社登録</Link></div>
                                    <div className={styles.menuItem}><Link to={'/accountManagement'}>アカウント管理</Link></div>
                                </div>
                                ):(
                                (auth.getRole() == 'administrator' ? (
                                    <div className={styles.menu}>
                                        <div className={styles.menuItem}><Link to={'/logout'}>ログアウト</Link></div>
                                        <div className={styles.menuItem}><Link to={'/accountManagementIndividual'}>アカウント管理</Link></div>
                                        <div className={styles.menuItem}><Link to={'/socialConnect'}>ソーシャルコネクト</Link></div>
                                        <div className={styles.menuItem}><Link to={'/webExploration'}>Web探索</Link></div>
                                        <div className={styles.menuItem}><Link to={'/graphLab'}>グラフ ラボ</Link></div>
                                        <div className={styles.menuItemSearchIcon}><span onClick={this.searchEngineer.bind(this)}><img src="../icon/mono_icons/search32.png" className={styles.menuItemSearchIconImg}/></span></div>
                                        <div className={styles.menuItemSearch}><input type="text" placeholder="タグ検索キーワード" ref="search" className={styles.menuItemSearchInput}/> </div>
                                    </div>
                                ):(
                                    <div className={styles.menu}>
                                        <div className={styles.menuItem}><Link to={'/logout'}>ログアウト</Link></div>
                                        <div className={styles.menuItem}><Link to={'/accountManagementIndividual'}>アカウント管理</Link></div>
                                        <div className={styles.menuItem}><Link to={'/socialConnect'}>ソーシャルコネクト</Link></div>
                                        <div className={styles.menuItem}><Link to={'/webExploration'}>Web探索</Link></div>
                                        <div className={styles.menuItem}><Link to={'/graphLab'}>グラフ ラボ</Link></div>
                                    </div>
                                ))
                                )
                            )
                        ) : (
                                <div className={styles.menu}>
                                    <div className={styles.menuItem}><Link to={'/register'}>登録</Link></div>
                                    <div className={styles.menuItem}><Link to={'/login'}>サインイン</Link></div>
                                </div>
                        )}
                    </div>
                </Sticky>
            </Sticky>

            <div className={styles.body}>
                {this.props.children}
            </div>
            <Loading ref="loading"/>
          </StickyContainer>
        )
      }
}

App.contextTypes = {
            router: React.PropTypes.object
};