import * as React from 'react';
import * as ReactDOM from 'react-dom';
import Top from "./top";

import { DefaultRoute, Link, Route, RouteHandler } from 'react-router';
import { StickyContainer, Sticky } from 'react-sticky';
import * as styles from './../../css/structure.css';
import auth from "./../../component/auth/auth";
import Loading from './../../component/loader/loading';
import PopupMessage from './../../component/popupMessage/popupMessage';
import $ from 'jquery';

export default class App extends React.Component<Props, {}> {

      constructor(props) {
        super(props);
        this.state = {
         loggedIn: auth.loggedIn()
        };

        this.searchTag = this.searchTag.bind(this);

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

      searchTag(e) {
            e.preventDefault();

            var self = this;
            self.refs.loading.openModal();
            self.refs.popupMessage.showMessage("now searching...");

            var data = {
                companyid: auth.getCompanyid(),
                userid:auth.getUserid(),
                token: auth.getToken(),
                code:10,
                query: this.refs.search.value
            };

            $.ajax({
                type:"post",
                url:"../commonModules/php/modules/GML.php/gml/model/search",
                data:JSON.stringify(data),
                contentType: 'application/json',
                dataType: "json",
                success: function(json_data) {
                    // React query convert an array with only one element to an string type data so that push one dummy data to keep it as an array
                    self.refs.loading.closeModal();
                    self.refs.popupMessage.closeMessage("finished searching !");

                    // This logic is actually ugly. I have not found a way to catch an event if the clicked link is the current page.
                    // If the current page is the link we click, React just renders the same UI. So, it cannot show the new search result
                    if(self.props.location.pathname == "/searchResult1"){
                        self.context.router.push(
                        {
                            pathname: "/searchResult2",
                            state: { modelInfo:  json_data.body.result }
                        });
                    }else if(self.props.location.pathname == "/searchResult2"){

                        self.context.router.push(
                        {
                            pathname: "/searchResult1",
                            state: { modelInfo:  json_data.body.result }
                        });
                    }else{
                        self.context.router.push(
                        {
                            pathname: "/searchResult1",
                            state: { modelInfo:  json_data.body.result }
                        });
                    }
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
                        <div className={styles.menuItemUserId}>Hello, {auth.getUserid()}</div>
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
                                        <div className={styles.menuItem}><Link to={'/graphLabLocalRepository'}>グラフ ローカルレポジトリ</Link></div>
                                        <div className={styles.menuItemSearch}><input type="text" placeholder="タグ検索キーワード" ref="search" className={styles.menuItemSearchInput}/> </div><div className={styles.menuItemSearchIcon}><span onClick={this.searchTag.bind(this)}><img src="../icon/mono_icons/search32.png" className={styles.menuItemSearchIconImg}/></span></div>
                                    </div>
                                ):(
                                    <div className={styles.menu}>
                                        <div className={styles.menuItem}><Link to={'/logout'}>ログアウト</Link></div>
                                        <div className={styles.menuItem}><Link to={'/accountManagementIndividual'}>アカウント管理</Link></div>
                                        <div className={styles.menuItem}><Link to={'/socialConnect'}>ソーシャルコネクト</Link></div>
                                        <div className={styles.menuItem}><Link to={'/webExploration'}>Web探索</Link></div>
                                        <div className={styles.menuItem}><Link to={'/graphLab'}>グラフ ラボ</Link></div>
                                        <div className={styles.menuItem}><Link to={'/graphLabLocalRepository'}>グラフ ローカルレポジトリ</Link></div>
                                        <div className={styles.menuItemSearch}><input type="text" placeholder="タグ検索キーワード" ref="search" className={styles.menuItemSearchInput}/> </div><div className={styles.menuItemSearchIcon}><span onClick={this.searchTag.bind(this)}><img src="../icon/mono_icons/search32.png" className={styles.menuItemSearchIconImg}/></span></div>
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
            <PopupMessage ref="popupMessage"/>
          </StickyContainer>
        )
      }
}

App.contextTypes = {
    router: React.PropTypes.object
};