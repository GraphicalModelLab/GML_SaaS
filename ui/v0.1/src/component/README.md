
## Directory Structure

| folder        | Description           |
| ------------- |-------------|
| accountManagement |   |
| accountManagementIndividual |   |
| auth |  javascript handling authentication stuff is store here. Every component accessing backend service is using this component to get some credential like access token. |
| chatBox | this component is for chatBox functionality. Not finished yet.  |
| companyRegistration | this component is not used now. |
| constant | Various constants are defined here. Now, just kinds of Graph shape are defined here. |
| dataExtractor | This is UI for Data Exploration menu |
| graphLab | This is UI for Graph Design menu where users design graph and do some experiment. |
| graphLabLocalRepository | This is UI showing what graphs have been saved by users. Users save graph in "graphLab" component (i.e. Graph Design Menu) |
| graphLabRepository | This is UI showing a record of saved graphs, i.e. one line of search result and local respository |
| loader | This is UI showing a loader |
| modelHistory | This is UI show a test history. You can see this component in Graph Design menu. |
| notFound | This is UI showing "notFound". Local Repository and search functionality shows this component when there is no data. |
| popupMessage | This is UI showing some message in UI when there is some error. For example, when users try to train some model without enough settings, then you see some error message in UI. |
| portal | Currently, not used |
| searchResult | Search functionality use this components to render the serach result. |
| socialConnect | This is UI for social connect menu. Users can connect to some social data source like Facebook and Google. After users connect to these sources, users can explore the data in Data Exploration menu. |
