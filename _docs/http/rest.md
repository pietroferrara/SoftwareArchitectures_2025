---
title: 2. HTTP REST
category: 05. HTTP
exclude: true
order: 2
---
<h2>Contents</h2>
* toc
{:toc}
## Introduction
In the previous section, we learned what is HTTP and how it works by talking about HTTP methods and status codes. 
Now, we want to introduce an architectural style that defines some constraints for how the architecture of a web system should work: the <a href="https://ics.uci.edu/~fielding/pubs/dissertation/fielding_dissertation.pdf">REST</a> (Representational state transfer) architecture.  
Introduced in 2000 by Roy Fielding during his PhD, the focal point of REST is the definition of resources. Resources are any information that can be named (a document, an image, a collection of resources, an application's domain object). A resource has an identifier, that permits it to identify it. With a resource identifier, one can refer to the resource and perform some operations on it. It is easy to explain it by showing an example. 
## How to perform HTTP requests
To test HTTP Calls, one can use <a href="https://curl.se/">cURL</a>, <a href="https://www.postman.com/">Postman</a>, or <a href="https://insomnia.rest/">Insomnia</a>. For simplicity, we use Insomnia.
## Example of an HTTP REST API
Let's see a real example of a REST API: <a href="https://docs.github.com/en/rest?apiVersion=2022-11-28">github</a> API. Please have a look at the <a href="https://docs.github.com/en/rest?apiVersion=2022-11-28">github</a>: documentation is important when you are dealing with software. For this case, we treat the GitHub API as a black box: we don't know how it works internally, how to access resources, or how to authenticate ourselves. These pieces of information, for good software, must be present clearly in the documentation. GitHub developers are good developers, and they have written all the necessary information.  
In this <a href="https://docs.github.com/en/rest/overview/resources-in-the-rest-api?apiVersion=2022-11-28"> there is written that the API is accessed from <a href="https://api.github.com">https://api.github.com</a>. So let's try to write a simple HTTP GET request to this URL to see what happens:
![HTTP rest]({{ site.baseurl }}/images/http_rest_1.png)
{% highlight json %}
{
    "current_user_url": "https://api.github.com/user",
    "current_user_authorizations_html_url": "https://github.com/settings/connections/applications{/client_id}",
    "authorizations_url": "https://api.github.com/authorizations",
    "code_search_url": "https://api.github.com/search/code?q={query}{&page,per_page,sort,order}",
    "commit_search_url": "https://api.github.com/search/commits?q={query}{&page,per_page,sort,order}",
    "emails_url": "https://api.github.com/user/emails",
    "emojis_url": "https://api.github.com/emojis",
    "events_url": "https://api.github.com/events",
    "feeds_url": "https://api.github.com/feeds",
    "followers_url": "https://api.github.com/user/followers",
    "following_url": "https://api.github.com/user/following{/target}",
    "gists_url": "https://api.github.com/gists{/gist_id}",
    "hub_url": "https://api.github.com/hub",
    "issue_search_url": "https://api.github.com/search/issues?q={query}{&page,per_page,sort,order}",
    "issues_url": "https://api.github.com/issues",
    "keys_url": "https://api.github.com/user/keys",
    "label_search_url": "https://api.github.com/search/labels?q={query}&repository_id={repository_id}{&page,per_page}",
    "notifications_url": "https://api.github.com/notifications",
    "organization_url": "https://api.github.com/orgs/{org}",
    "organization_repositories_url": "https://api.github.com/orgs/{org}/repos{?type,page,per_page,sort}",
    "organization_teams_url": "https://api.github.com/orgs/{org}/teams",
    "public_gists_url": "https://api.github.com/gists/public",
    "rate_limit_url": "https://api.github.com/rate_limit",
    "repository_url": "https://api.github.com/repos/{owner}/{repo}",
    "repository_search_url": "https://api.github.com/search/repositories?q={query}{&page,per_page,sort,order}",
    "current_user_repositories_url": "https://api.github.com/user/repos{?type,page,per_page,sort}",
    "starred_url": "https://api.github.com/user/starred{/owner}{/repo}",
    "starred_gists_url": "https://api.github.com/gists/starred",
    "topic_search_url": "https://api.github.com/search/topics?q={query}{&page,per_page}",
    "user_url": "https://api.github.com/users/{user}",
    "user_organizations_url": "https://api.github.com/user/orgs",
    "user_repositories_url": "https://api.github.com/users/{user}/repos{?type,page,per_page,sort}",
    "user_search_url": "https://api.github.com/search/users?q={query}{&page,per_page,sort,order}"
}
{% endhighlight %}
Our request returns a 200 (OK) status code, and we have a JSON body plus some headers.
![HTTP rest]({{ site.baseurl }}/images/http_rest_2.png)
As we can notice, the response consists of a set of URLs. These URLs are pointers to resources. Since we are curious, let's see what happens if we try to access the first resource (that is, using the intuitions, the resource that models our GitHub profile).  
![HTTP rest]({{ site.baseurl }}/images/http_rest_3.png)
Oh, something is not working! We get an error 401 (Unauthorized). However, the call returns a body with some information about the error. To have a good API, the server should provide some useful information about the problem to help the client understand what happens and to direct him on how to behave.  401 means that we are not authorized. The documentation says that we can use a Bearer Access Token to have a grant. We can create one here: <a href="https://github.com/settings/tokens">https://github.com/settings/tokens</a> (select repo, user, project scope: scope defines what you can do and what not with this token).  
![HTTP rest]({{ site.baseurl }}/images/http_rest_4.png)
![HTTP rest]({{ site.baseurl }}/images/http_rest_5.png)
Now copy the Token, and paste it into a Header (Authorization: Bearer XXXXXXXXXXXX):
![HTTP rest]({{ site.baseurl }}/images/http_rest_6.png)
After doing this, click send: now we have the grant to access the resource. The call responds 200 (OK) with the resource in the Body. Note that the response differs (every one of them has a different access token that identifies different GitHub accounts).   
![HTTP rest]({{ site.baseurl }}/images/http_rest_7.png)
How to see all the repositories of a user?
![HTTP rest]({{ site.baseurl }}/images/http_rest_8.png)
The Call goes in 200 (OK), but our response is an empty array: this means that this account does not have any repositories. Let's try to create a repository from the GitHub website to see what changes.
![HTTP rest]({{ site.baseurl }}/images/http_rest_9.png)
![HTTP rest]({{ site.baseurl }}/images/http_rest_10.png)
{% highlight json %}
[
    {
        "id": 699273551,
        "node_id": "R_kgDOKa4RTw",
        "name": "test-repo",
        "full_name": "gzanatestgithub/test-repo",
        "private": true,
        "owner": {
            "login": "gzanatestgithub",
            "id": 146717727,
            "node_id": "U_kgDOCL68Hw",
            "avatar_url": "https://avatars.githubusercontent.com/u/146717727?v=4",
            "gravatar_id": "",
            "url": "https://api.github.com/users/gzanatestgithub",
            "html_url": "https://github.com/gzanatestgithub",
            "followers_url": "https://api.github.com/users/gzanatestgithub/followers",
            "following_url": "https://api.github.com/users/gzanatestgithub/following{/other_user}",
            "gists_url": "https://api.github.com/users/gzanatestgithub/gists{/gist_id}",
            "starred_url": "https://api.github.com/users/gzanatestgithub/starred{/owner}{/repo}",
            "subscriptions_url": "https://api.github.com/users/gzanatestgithub/subscriptions",
            "organizations_url": "https://api.github.com/users/gzanatestgithub/orgs",
            "repos_url": "https://api.github.com/users/gzanatestgithub/repos",
            "events_url": "https://api.github.com/users/gzanatestgithub/events{/privacy}",
            "received_events_url": "https://api.github.com/users/gzanatestgithub/received_events",
            "type": "User",
            "site_admin": false
        },
        "html_url": "https://github.com/gzanatestgithub/test-repo",
        "description": null,
        "fork": false,
        "url": "https://api.github.com/repos/gzanatestgithub/test-repo",
        "forks_url": "https://api.github.com/repos/gzanatestgithub/test-repo/forks",
        "keys_url": "https://api.github.com/repos/gzanatestgithub/test-repo/keys{/key_id}",
        "collaborators_url": "https://api.github.com/repos/gzanatestgithub/test-repo/collaborators{/collaborator}",
        "teams_url": "https://api.github.com/repos/gzanatestgithub/test-repo/teams",
        "hooks_url": "https://api.github.com/repos/gzanatestgithub/test-repo/hooks",
        "issue_events_url": "https://api.github.com/repos/gzanatestgithub/test-repo/issues/events{/number}",
        "events_url": "https://api.github.com/repos/gzanatestgithub/test-repo/events",
        "assignees_url": "https://api.github.com/repos/gzanatestgithub/test-repo/assignees{/user}",
        "branches_url": "https://api.github.com/repos/gzanatestgithub/test-repo/branches{/branch}",
        "tags_url": "https://api.github.com/repos/gzanatestgithub/test-repo/tags",
        "blobs_url": "https://api.github.com/repos/gzanatestgithub/test-repo/git/blobs{/sha}",
        "git_tags_url": "https://api.github.com/repos/gzanatestgithub/test-repo/git/tags{/sha}",
        "git_refs_url": "https://api.github.com/repos/gzanatestgithub/test-repo/git/refs{/sha}",
        "trees_url": "https://api.github.com/repos/gzanatestgithub/test-repo/git/trees{/sha}",
        "statuses_url": "https://api.github.com/repos/gzanatestgithub/test-repo/statuses/{sha}",
        "languages_url": "https://api.github.com/repos/gzanatestgithub/test-repo/languages",
        "stargazers_url": "https://api.github.com/repos/gzanatestgithub/test-repo/stargazers",
        "contributors_url": "https://api.github.com/repos/gzanatestgithub/test-repo/contributors",
        "subscribers_url": "https://api.github.com/repos/gzanatestgithub/test-repo/subscribers",
        "subscription_url": "https://api.github.com/repos/gzanatestgithub/test-repo/subscription",
        "commits_url": "https://api.github.com/repos/gzanatestgithub/test-repo/commits{/sha}",
        "git_commits_url": "https://api.github.com/repos/gzanatestgithub/test-repo/git/commits{/sha}",
        "comments_url": "https://api.github.com/repos/gzanatestgithub/test-repo/comments{/number}",
        "issue_comment_url": "https://api.github.com/repos/gzanatestgithub/test-repo/issues/comments{/number}",
        "contents_url": "https://api.github.com/repos/gzanatestgithub/test-repo/contents/{+path}",
        "compare_url": "https://api.github.com/repos/gzanatestgithub/test-repo/compare/{base}...{head}",
        "merges_url": "https://api.github.com/repos/gzanatestgithub/test-repo/merges",
        "archive_url": "https://api.github.com/repos/gzanatestgithub/test-repo/{archive_format}{/ref}",
        "downloads_url": "https://api.github.com/repos/gzanatestgithub/test-repo/downloads",
        "issues_url": "https://api.github.com/repos/gzanatestgithub/test-repo/issues{/number}",
        "pulls_url": "https://api.github.com/repos/gzanatestgithub/test-repo/pulls{/number}",
        "milestones_url": "https://api.github.com/repos/gzanatestgithub/test-repo/milestones{/number}",
        "notifications_url": "https://api.github.com/repos/gzanatestgithub/test-repo/notifications{?since,all,participating}",
        "labels_url": "https://api.github.com/repos/gzanatestgithub/test-repo/labels{/name}",
        "releases_url": "https://api.github.com/repos/gzanatestgithub/test-repo/releases{/id}",
        "deployments_url": "https://api.github.com/repos/gzanatestgithub/test-repo/deployments",
        "created_at": "2023-10-02T09:54:41Z",
        "updated_at": "2023-10-02T09:54:41Z",
        "pushed_at": "2023-10-02T09:54:41Z",
        "git_url": "git://github.com/gzanatestgithub/test-repo.git",
        "ssh_url": "git@github.com:gzanatestgithub/test-repo.git",
        "clone_url": "https://github.com/gzanatestgithub/test-repo.git",
        "svn_url": "https://github.com/gzanatestgithub/test-repo",
        "homepage": null,
        "size": 0,
        "stargazers_count": 0,
        "watchers_count": 0,
        "language": null,
        "has_issues": true,
        "has_projects": true,
        "has_downloads": true,
        "has_wiki": false,
        "has_pages": false,
        "has_discussions": false,
        "forks_count": 0,
        "mirror_url": null,
        "archived": false,
        "disabled": false,
        "open_issues_count": 0,
        "license": null,
        "allow_forking": true,
        "is_template": false,
        "web_commit_signoff_required": false,
        "topics": [],
        "visibility": "private",
        "forks": 0,
        "open_issues": 0,
        "watchers": 0,
        "default_branch": "main",
        "permissions": {
            "admin": true,
            "maintain": true,
            "push": true,
            "triage": true,
            "pull": true
        }
    }
]
{% endhighlight %}
The returned JSON now contains our repository: look at the entry. You have a field "owner", that says who is the owner of the repository, with URLs that identify resources, and some information: for example, we can know that this repository is private, and we have URLs that permit access to underlying resources such as merge requests, commits, etc.  
Let's try to create an issue from the API. <a href="https://docs.github.com/en/rest/issues/issues#create-an-issue">Here</a>, we have all the necessary information about how to build a request to do this. We know that we need to perform a POST a body to **/repos/{owner}/{repo}/issues** where *owner* is the id of the owner of the repository and *repo* is the name of the repo (this is like to say: from user 'owner', get the repository named 'repo', and POST a new issue). Let's create a minimal body with only the required fields (change owner and repo with your data):
{% highlight json %}
{
    "owner": "gzanatestgithub",
    "repo": "test-repo",
    "title": "[BUG] Buffer overflow"
}
{% endhighlight %}
![HTTP rest]({{ site.baseurl }}/images/http_rest_11.png)
Status 201 (Created) means that our POST request was successful. We can see the created issue from GitHub:
![HTTP rest]({{ site.baseurl }}/images/http_rest_12.png)
So that's all. This is how you interact with an API that relies on the REST style. We use URLs to access particular resources and we use HTTP methods to manipulate them. The main idea is that every resource is unique and addressed by a link. 
## REST vs RPC: what's the difference?
We conclude this section by talking a bit about the main difference between RPC and REST.  
RPC stands for Remote Procedure Call and consists of executing some functions (procedures) in a different address space (for example, in another machine), without requiring a programmer to explicitly handle the interaction between systems. For example, in python:  


{% highlight python %}
# SERVER
import xmlrpc.server
class Server:
    def add(self, a, b):
        return a + b

server = xmlrpc.server.SimpleXMLRPCServer(("localhost", 8000))
server.register_instance(Server())
server.serve_forever()
{% endhighlight %}
{% highlight python %}
# CLIENT
import xmlrpc.client

server = xmlrpc.client.ServerProxy("http://localhost:8000")
result = server.add(2, 3)
print(result)
{% endhighlight %}
Without going into too much detail, the client will call the add function on the server (like it is a "local" function) and print the result (the sum operation is executed on the server). 
RPC and REST could seem very similar (for example, considering GitHub, one could use an API library <a href="https://github.com/PyGithub/PyGithub">this</a> that hides the REST architecture), but the main difference is that RPC exposes functions, while REST exposes resources and a way to manipulate them. RPC is used when you want to execute something on a server, and REST is typically used to perform CRUD (Create, Retrieve, Update, Delete) operations on remote data.
## Exercises
1. Try a PATCH operation on your previously created issue, for example adding a description (body field) or editing the title. Try also to add a comment to the issue. Now, remove the comment.
1. Write a script with your preferred language that, taking in input the owner and the name of a public repository, downloads the .zip of the latest tag.
<div>
Previous: <a href="/SoftwareArchitectures_2025/http/introduction">HTTP - Introduction</a> 
</div>
<div>
Next: <a href="/SoftwareArchitectures_2025/http/spring-rest">HTTP - Spring REST</a>  
</div>
