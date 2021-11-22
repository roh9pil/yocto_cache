# Hello BitBake
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
        
1. Setting `BBPATH`

   `export BBPATH="project"`

   `mkdir project` 

2. Run Bitbake
