import { Directive, ElementRef, OnDestroy, OnInit } from '@angular/core';

@Directive({
  selector: '[appBodyPortal]',
  standalone: true,
})
export class BodyPortalDirective implements OnInit, OnDestroy {
  constructor(private el: ElementRef<HTMLElement>) {}

  ngOnInit(): void {
    if (typeof document !== 'undefined' && document.body) {
      document.body.appendChild(this.el.nativeElement);
    }
  }

  ngOnDestroy(): void {
    if (typeof document !== 'undefined' && document.body) {
      const el = this.el.nativeElement;
      if (el.parentNode === document.body) {
        document.body.removeChild(el);
      }
    }
  }
}
