import {
  animate,
  group,
  keyframes,
  query,
  stagger,
  state,
  style,
  transition,
  trigger,
} from '@angular/animations';

export let loginMove = trigger('move', [
  state('start', style({ left: '0%' })),
  state('end', style({ left: '60%' })),
  transition('start <=> end', [
    group([
      animate('1s ease-in-out'),
      query('#firstP, h1', [
        animate(
          '0.5s',
          keyframes([style({ opacity: 0 }), style({ opacity: 1 })])
        ),
      ]),
    ]),
  ]),
]);

export let fadeIn = trigger('FadeIn', [
  state('void', style({ opacity: 0, transform: 'scale(0.5)' })),
  transition(':enter, :leave', [animate('1s ease-in-out')]),
]);

export let moveAnimation = trigger('moveAnimation', [
  transition(':enter', [
    query('section, mat-expansion-panel', [
      style({ opacity: 0, transform: 'translateY(200px)' }),
      stagger(100, [
        animate(
          '500ms cubic-bezier(0.35, 0, 0.25, 1)',
          style({ opacity: 1, transform: 'none' })
        ),
      ]),
    ]),
  ]),
]);

export let panelAnimation = trigger('panelAnimation', [
  transition(':enter', [
    style({ opacity: 0, transform: 'translateY(200px)' }),
    animate(
      '500ms cubic-bezier(0.35, 0, 0.25, 1)',
      style({ opacity: 1, transform: 'none' })
    ),
  ]),
]);

export let expandHeight = trigger('expandHeight', [
  transition(':enter', [
    style({ overflow: 'hidden', height: '0rem' }),
    animate('0.5s ease-in-out', style({ height: '11rem' })),
  ]),
]);

export let moveForm = trigger('moveForm', [
  transition(':enter', [
    style({ opacity: 0, transform: 'translateX(50px)' }),
    animate(
      '0.7s ease-in-out',
      style({ opacity: 1, transform: 'translateX(0px)' })
    ),
  ]),
  transition(':leave', [
    animate(
      '0.7s ease-in-out',
      style({ opacity: 0, transform: 'translateX(100px)' })
    ),
  ]),
]);

export let moveInfoPerfil = trigger('moveInfoPerfil', [
  state('center', style({ left: '25%' })),
  state('left', style({ left: '0' })),
  transition('center <=> left', [animate('0.7s ease-in-out')]),
]);
export let opacity = trigger('opacity', [
  state('center', style({ opacity: 0, left: '25%' })),
  state('left', style({ opacity: 1, left: '0' })),
  transition('center <=> left', [animate('0.7s ease-in-out')]),
]);

export let initCard = trigger('initCard', [
  transition(':enter, :leave', [
    style({ opacity: 0, scale: 1.4 }),
    animate('0.5s 24s ease-in-out'),
  ]),
]);

export let textHeader = trigger('textHeader', [
  transition(':enter, :leave', [
    style({ opacity: 0 }),
    animate('0.5s 20s ease-in-out'),
    style({ opacity: 1, top: '40%', scale: 1.5 }),
    animate('0.4s 3.5s ease-in-out'),
  ]),
]);

export let startText = trigger('startText', [
  transition(':enter, :leave', [
    style({ opacity: 0, display:'flex'}),
    animate('0.5s 12.5s ease-in'),
    style({ opacity: 1 }),
    animate('0.5s 6s ease-out', style({ opacity: 0 })),
  ]),
]);
export let contentFade = trigger('contentFade', [
  transition(':enter', [style({ opacity: 0 }), animate('0.5s ease-in-out')]),
]);

export let fade = trigger('fade', [
  transition(':enter', [
     style({ opacity: 1 }),
    group([
      query('#lineAnimation', [
        style({ height: '0%', top: '2rem' }),
        animate(
          '1s 1s cubic-bezier( 0, 1, 0.5, 1 )',
          style({ height: '90%', top: '2rem' })
        ),
        animate(
          '1s 1s cubic-bezier( 0, 1, 0.5, 1 )',
          style({ height: '25%', top: '8.5rem' })
        ),
        animate(
          '0.5s 1s cubic-bezier( 0, 1, 0.5, 1 )',
          style({ height: '0.5%', top: '16.5rem' })
        ),
        animate('0.5s cubic-bezier( 0, 1, 0.5, 1 )', style({ width: '50%' })),
        animate(
          '0.5s 1.5s cubic-bezier( 0, 1, 0.5, 1 )',
          style({ width: '0.5%' })
        ),
        animate(
          '0.5s cubic-bezier( 0, 1, 0.5, 1 )',
          style({ height: '22%', top: '9rem' })
        ),
      ]),

      query('#imgPresentation', [
        style({ translate: '36rem' }),
        animate('1s 2s cubic-bezier( 0, 1, 0.5, 1 )'),
      ]),

      query('#textPresentation', [
        query('#text', [style({ translate: '0 -4rem' })]),
        style({ translate: '-40rem' }),
        animate('1s 4.3s cubic-bezier( 0, 1, 0.5, 1 )'),
        query('#text', [animate('1s 1s cubic-bezier( 0, 1, 0.5, 1 )')]),
      ]),

    ]),
    animate('0.5s 3s ease-in-out', style({ opacity: 0 })),
  ]),
]);
