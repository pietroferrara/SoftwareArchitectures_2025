---
title: Home
---

<h2>Contents</h2>
<ul>
    {% assign grouped = site.docs | group_by: 'category' | sort: 'name' %}
    {% for group in grouped %}
        {% assign visible_items = "" | split: "" %}
        {% for item in group.items %}
            {% unless item.exclude %}
                {% assign visible_items = visible_items | push: item %}
            {% endunless %}
        {% endfor %}
        
        {% if visible_items.size > 0 %}
            <li class="">
                <a class="content-link" href="{{ site.baseurl }}{{ visible_items.first.url }}">{{ group.name }}</a>
                <ul>
                    {% for item in visible_items %}
                        <li class=""><a class="content-link" href="{{ site.baseurl }}{{ item.url }}">{{ item.title }}</a></li>
                    {% endfor %}
                </ul>
            </li>
        {% endif %}
    {% endfor %}
</ul>