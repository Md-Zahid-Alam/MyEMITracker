# Finance design system — Version 9

Version 8.0 uses the **Calm Financial** direction: trustworthy teal, quiet neutral surfaces, strong hierarchy, restrained elevation and accessible semantic status colors.

## Premium component rules

- Main pages own their title; a redundant global title bar is not shown.
- Hero cards are reserved for the single most important value on a page.
- Standard cards use low elevation and consistent rounded shapes.
- Navigation cards contain a title, supporting text and trailing affordance.
- Selection controls use compact pills and a light teal selected state.
- Forms use a maximum width of 680 dp and 56 dp minimum controls.
- Destructive, warning, positive and information states use semantic colors.
- Large Android windows constrain content width instead of stretching controls edge to edge.

The visual language is defined by `core/designsystem/FinanceDesignSystem.kt`.

- Primary accent: accessible teal.
- Light and dark semantic color schemes.
- Material 3 typography and component behavior.
- Central small, medium, large and pill shapes.
- Six spacing tokens plus screen, section and minimum touch-target tokens.
- Components inherit semantic content colors; screens must not use black/white as layout colors.
- User-entered financial information is never translated or recolored as status text.

Future UI changes must reuse these tokens instead of adding isolated colors or arbitrary dimensions.

## Version 9 report and regional components

- Country selection uses a searchable offline ISO catalog, generated flag, currency metadata and Bengali/English display names.
- Reports share product identity, teal hierarchy, document metadata and currency context.
- Excel uses dedicated dashboard, EMI, loan, debt-direction, expense, payment and payment-request sheets with frozen/filterable headers.
- PDF input is converted into semantic headings, fields, paragraphs, tables and signatures before rendering.
