# Hello BitBake
```
.
├── layer
│   ├── conf
│   │   └── layer.conf
│   └── hello_bitbake.bb
└── project
    ├── classes
    │   └── base.bbclass
    └── conf
        ├── bblayers.conf
        └── bitbake.conf
```        
1. Setting an environment variable, `BBPATH`

   `export BBPATH="project"`

2. Seting `BBLAYER` in conf/bbayser.conf
