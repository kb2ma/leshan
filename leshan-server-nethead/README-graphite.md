This document describes how to set up a Graphite + Grafana network monitoring solution for a Debian based system.

Based on these instructions:

* [Graphite project](http://graphite.readthedocs.io/en/latest/index.html)
* [*Monitoring with Graphite*](http://shop.oreilly.com/product/0636920035794.do) book
* Debian package documentation for graphite-web, in `/usr/share/doc/graphite-web/README.debian`
* [Grafana docs](http://shop.oreilly.com/product/0636920035794.do)


Graphite
========

For a Debian-based system, install these packages:

* build-essential
* python-dev
* graphite-carbon (installs python-whisper)
* graphite-web (installs python-django); must use at least v1.1.4-3
* apache2 (used by graphite-web)
* libapache2-mod-wsgi-py3

Configuration
-------------

**`/etc/carbon/storage-schemas.conf`**

Configure interval and retention. Below is a good starting point.

```
[temp]
pattern = ^temp\.
retentions = 60s:90d
```

**`/etc/graphite/local_settings.py`**

Update the following settings for your local setup.

```
SECRET_KEY = 'somelonganduniquesecretstring'
TIME_ZONE = 'America/New_York'
```

**Django database**

Initializes the graphite database:

```
$ sudo su -s /bin/bash _graphite -c 'graphite-manage migrate'
```

When I ran the above command, the output was as shown below.
```
Operations to perform:
  Apply all migrations: account, admin, auth, contenttypes, dashboard, events, sessions, tagging, tags, url_shortener
Running migrations:
  Applying contenttypes.0001_initial... OK
  Applying auth.0001_initial... OK
  Applying account.0001_initial... OK
  Applying admin.0001_initial... OK
  Applying admin.0002_logentry_remove_auto_add... OK
  Applying contenttypes.0002_remove_content_type_name... OK
  Applying auth.0002_alter_permission_name_max_length... OK
  Applying auth.0003_alter_user_email_max_length... OK
  Applying auth.0004_alter_user_username_opts... OK
  Applying auth.0005_alter_user_last_login_null... OK
  Applying auth.0006_require_contenttypes_0002... OK
  Applying auth.0007_alter_validators_add_error_messages... OK
  Applying auth.0008_alter_user_username_max_length... OK
  Applying dashboard.0001_initial... OK
  Applying events.0001_initial... OK
  Applying sessions.0001_initial... OK
  Applying tagging.0001_initial... OK
  Applying tagging.0002_on_delete... OK
  Applying tags.0001_initial... OK
  Applying url_shortener.0001_initial... OK
```

**Apache**

```
$ sudo cp /usr/share/graphite-web/apache2-graphite.conf /etc/apache2/sites-available

```

Finally, disable the default Apache site and enable the Graphite site:
```
$ sudo a2dissite 000-default
$ sudo a2ensite apache2-graphite
$ sudo systemctl reload apache2
```

Running
-------

### Ports

* 2003 TCP/UDP -- Plaintext input
* 2004 TCP -- Pickle input
* 7002 -- Cache query for output

### Data

* `/var/lib/graphite/whisper`

The tree of stored metrics is below the `whisper` directory. You can review the entries to verify the expected data has been saved.

### Logs

* `/var/log/carbon`
* `/var/log/apache2/graphite-web_access.log`
* `/var/log/apache2/graphite-web_error.log`
* `/var/log/graphite`
`

In `/var/log/carbon/console.log`, you may see lines like below. Since `storage-aggregation.conf` is optional, the lines are not an issue.

```
01/01/2020 12:59:37 :: /etc/carbon/storage-aggregation.conf not found or wrong perms, ignoring.
```

In `/var/log/carbon/tagsdb.log`, you may see lines like below. The errors relate to graphite's new tagging capability. I think the errors are just an artifact from installation. However, you may wish to turn off tags in `carbon.conf` with `ENABLE_TAGS = False`.

```
01/01/2020 13:53:59 :: Error tagging carbon.agents.localhost-a.committedPoints: Error response 404 from http://127.0.0.1:80/tags/tagMultiSeries
01/01/2020 13:59:59 :: Error tagging carbon.agents.localhost-a.metricsReceived: Error response 404 from http://127.0.0.1:80/tags/tagMultiSeries
01/01/2020 14:04:59 :: Error tagging carbon.agents.localhost-a.pointsPerUpdate: Error response 404 from http://127.0.0.1:80/tags/tagMultiSeries
01/01/2020 14:10:59 :: Error tagging carbon.agents.localhost-a.cache.overflow: Error response 404 from http://127.0.0.1:80/tags/tagMultiSeries
```


Users:

* _graphite

Metric Names
------------

Graphite uses a dotted name for each metric, like `temp.3303.0`. This name refers to the temperature reading for instance 0. 

It is important to structure the metric name in a consistent hierarchical fashion. The first part of the name should contain static elements, and variable elements like node names should appear later. Matt Aimonetti's [post](https://matt.aimonetti.net/posts/2013/06/26/practical-guide-to-graphite-monitoring/)
provides useful guidelines.


Grafana Dashboard
=================

Install from the [download instructions](https://grafana.com/grafana/download). See the [installation instructions](http://docs.grafana.org/installation/debian/) for file locations.

Configuration
-------------

Create a data source for temp in the grafana dashboard. Be sure to use 'proxy' access to the data source.

Running
-------

The commands below, for systemd, start Grafana and setup to start on boot.

```
$ sudo systemctl daemon-reload
$ sudo systemctl enable grafana-server
$ sudo systemctl start grafana-server
$ sudo systemctl status grafana-server
```

Access at `http://localhost:3000`

View the log: `/var/log/grafana/grafana.log`
